(ns korean-utils.jamo
  (:refer-clojure :exclude [replace]))

(def unicode-offset 0xAC00) ;; 한글 유니코드 오프셋

(defn vector->sorted-map-with-item-and-idx
  [v]
  (->> v
       (map-indexed (fn [idx item] [item idx]))
       (into (sorted-map))))

(def choseongs (vector->sorted-map-with-item-and-idx ["ㄱ" "ㄲ" "ㄴ" "ㄷ" "ㄸ" "ㄹ" "ㅁ" "ㅂ" "ㅃ" "ㅅ"
                                                      "ㅆ" "ㅇ" "ㅈ" "ㅉ" "ㅊ" "ㅋ" "ㅌ" "ㅍ" "ㅎ"]))

(def jungseongs (vector->sorted-map-with-item-and-idx ["ㅏ" "ㅐ" "ㅑ" "ㅒ" "ㅓ" "ㅔ" "ㅕ" "ㅖ" "ㅗ" "ㅘ"
                                                       "ㅙ" "ㅚ" "ㅛ" "ㅜ" "ㅝ" "ㅞ" "ㅟ" "ㅠ" "ㅡ" "ㅢ" "ㅣ"]))

(def jongseongs (vector->sorted-map-with-item-and-idx ["" "ㄱ" "ㄲ" "ㄳ" "ㄴ" "ㄵ" "ㄶ" "ㄷ" "ㄹ" "ㄺ" "ㄻ" "ㄼ"
                                                       "ㄽ" "ㄾ" "ㄿ" "ㅀ" "ㅁ" "ㅂ" "ㅄ" "ㅅ" "ㅆ" "ㅇ" "ㅈ" "ㅊ" "ㅋ" "ㅌ" "ㅍ" "ㅎ"]))

(defn korean-syllable?
  [syllable]
  (>= (int syllable) unicode-offset))

(defn syllable->choseong-idx
  [syllable]
  (-> syllable
      int
      (- unicode-offset)
      (/ 28)
      (/ 21)
      int))

(defn syllable->jungseong-idx
  [syllable]
  (-> syllable
      int
      (- unicode-offset)
      (/ 28)
      (mod 21)
      int))

(defn syllable->jongseong-idx
  [syllable]
  (-> syllable
      int
      (- unicode-offset)
      (mod 28)))

(defn syllable->jamo-indices
  [syllable]
  {:cho-idx  (syllable->choseong-idx syllable)
   :jung-idx (syllable->jungseong-idx syllable)
   :jong-idx (syllable->jongseong-idx syllable)})

(defn ->syllable-with-jamo-indices
  [syllable]
  (let [korean-syllable? (korean-syllable? syllable)]
    (cond-> {:syllable         syllable
             :korean-syllable? korean-syllable?}
      korean-syllable? (merge (syllable->jamo-indices syllable)))))

(defn jamo->jamo-indices
  [jamo]
  {:cho-idx  (choseongs jamo)
   :jung-idx (jungseongs jamo)
   :jong-idx (jongseongs jamo)})

(defn get-replaced-jamo-idx
  [jamo-idx-type target-jamo-indices match-jamo-indices replacement-jamo-indices]
  (let [jamo-idx             (jamo-idx-type target-jamo-indices)
        match-jamo-idx       (jamo-idx-type match-jamo-indices)
        replacement-jamo-idx (jamo-idx-type replacement-jamo-indices)]
    (if (and (= jamo-idx match-jamo-idx)
             (some? replacement-jamo-idx))
      replacement-jamo-idx
      jamo-idx)))

(defn get-replaced-jamo-indices
  [target-jamo-indices match-jamo-indices replacement-jamo-indices]
  (let [jamo-idx-types [:cho-idx :jung-idx :jong-idx]]
    (->> jamo-idx-types
         (map #(get-replaced-jamo-idx % target-jamo-indices match-jamo-indices replacement-jamo-indices))
         (map vector jamo-idx-types)
         (into {}))))

(defn jamo-indices->syllable
  [{:keys [cho-idx jung-idx jong-idx]}]
  (-> cho-idx
      (* 21)
      (+ jung-idx)
      (* 28)
      (+ jong-idx)
      (+ unicode-offset)
      char))

(defn change-syllable
  [syllable-with-jamo-indices match replacement]
  (if (:korean-syllable? syllable-with-jamo-indices)
    (let [match-jamo-indices       (jamo->jamo-indices match)
          replacement-jamo-indices (jamo->jamo-indices replacement)
          replaced-jamo-indices    (get-replaced-jamo-indices syllable-with-jamo-indices match-jamo-indices replacement-jamo-indices)]
      (assoc syllable-with-jamo-indices :syllable (jamo-indices->syllable replaced-jamo-indices)))
    syllable-with-jamo-indices))

(defn verify-jamo
  [jamo]
  (let [jamos (merge choseongs jungseongs jongseongs)]
    (when-not (jamos jamo)
      (throw (ex-info "Not valid jamo" {:cause (format "%s is not valid jamo" jamo)})))))

(defn replace
  "주어진 문자열에서 match와 일치하는 자모를 replacement로 치환합니다."
  [s match replacement]
  (verify-jamo match)
  (verify-jamo replacement)
  (let [syllable-with-jamo-indices (map ->syllable-with-jamo-indices s)]
    (->> (map #(change-syllable % match replacement) syllable-with-jamo-indices)
         (map :syllable)
         (apply str))))

(comment
  (replace "안녕하세요 Hello 123" "ㅇ" "ㄱ"))
