(ns korean-utils.jamo-test
  (:require [clojure.test :refer [deftest run-tests is testing]]
            [korean-utils.jamo :as jamo]))

(deftest jamo-replace-test
  (testing "여러 자모를 동시에 치환할 수 있다."
    (is (= (jamo/replace "안녕하세요" "ㅇ" "ㄱ")
           "간녁하세교")))
  
  (testing "일치하는 치환 대상 자모가 없는 경우, 인풋 문자열이 그대로 반환된다."
    (is (= (jamo/replace "안녕하세요" "ㄹ" "ㄱ")
           "안녕하세요")))
  
  (testing "초성에 없고 종성에만 있는 자모로 치환하려 하는 경우, 종성만 치환된다."
    (is (= (jamo/replace "안녕하세요" "ㅇ" "ㄵ")
           "안녅하세요")))
  
  (testing "인풋 문자열에 한글 이외 문자가 섞여 있어도 자모는 정상적으로 치환되며, 나머지 문자는 그대로 유지된다."
    (is (= (jamo/replace "안녕하세요 Hello 123!!!" "ㅎ" "ㅇ")
           "안녕아세요 Hello 123!!!"))))

(comment
  (run-tests))

