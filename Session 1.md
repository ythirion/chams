# Session 1 - FizzBuzz
Kata to practice `T.D.D`.

Write a function that returns for a given number from 1 to 100 this given number, except that : 

- For multiples of 3 returns `Fizz`
- For the multiples of 5 returns `Buzz`
- For numbers which are multiples of both 3 and 5 returns `FizzBuzz`

![FizzBuzz](https://gblobscdn.gitbook.com/assets%2F-MAffO8xa1ZWmgZvfeK2%2F-MRjOd5QFjO3t1_uF11v%2F-MRjOzhK6HceS86U0Jt-%2Fimage.png?alt=media&token=2ac59d40-ac10-47b1-90ee-951609100f76)

### Example Mapping
[![Example Mapping](https://xtrem-tdd.netlify.app/assets/images/example-mapping-ef78551cd8afcf2f192a3ca752512616.webp)](https://xtrem-tdd.netlify.app/flavours/practices/example-mapping/)

Example:
[![Example Mapping du Bouchonnois](https://raw.githubusercontent.com/ythirion/refactoring-du-bouchonnois/main/example-mapping/example-mapping.webp)](https://github.com/ythirion/refactoring-du-bouchonnois)

### A little bit too easy?
* Remove “if” in your code
* Parameterize your FizzBuzz, implement this method : 
   * int limit : 100
   * int fizz : 3
   * int buzz : 5
* Extend your program
   * Multiples of 7 are “Whizz”
   * Multiples of 11 are “Bang”
* Create a Higher Order Function (fizzBuzz function takes an action function in args)
* Add a voice output
* Write it in an unknown language (still by using TDD)
* ...

## Reflect
- What happened to your code when implementing new tests?
- How many time did you spent debugging your code?