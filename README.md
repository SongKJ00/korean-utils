# Korean Utils
Clojure library to manipulate korean alphabets(called **"jamo"**)

## Supported features
### korean alphabets(jamo) replacement
#### How to use(in REPL)
```clj
(require '[korean-utils.jamo :as jamo])

(jamo/replace "안녕하세요" "ㅇ" "ㄱ")
;; => "간녁하세교"

(jamo/replace "안녕하세요" "ㅇ" "ㄵ")
;; => "안녅하세요"

(jamo/replace "안녕하세요 Hello 123!!!" "ㅎ" "ㅇ")
;; => "안녕아세요 Hello 123!!!"

(jamo/replace "샤인머스켓" "ㅔ" "ㅐ")
;; => "샤인머스캣"
```
