# MFPL - Minimalistic Functional Programming Language
[WORK IN PROGRESS]

Hello!
This language is aimed for beginners in functional programming and for people that are curious how an interpreter could be implemented.
It contains easy to grasp, closed type system with function currying and first-class citizenship. The interpreter itself
and the type checker are relatively small, so you could go and explore them by yourself. 

Currently the project contains the type checker and runtime interpreter, but in the future I will add parser and lexer. There is currently
no mechanism to work with tuples, like pattern matching, but this is also a feature to add.

## Immutability and structure 
Since this is a functional language, there is no mutability of variables. For example you can declare a variable of the same name (even with
different type), but you cant modify it's content (via a variable reference). 

Every instance of a type is a value - for example declaring a function yields a function runtime object under the hood.
If you declare a variable with the same name, then the previous variable gets overridden by newly declared variable. 

Every statement of a program is represented as a tree of nodes which in turn represent the basic building blocks of the language, like
operators, variable declarations and references.

## Important terms
Variable is basically a label by which we can look up values.

An executing object is an object, that travels along the abstract syntax tree nodes (tree of nodes). It does not necessarily execute the code. For 
example a type checker looks through the nodes and checks for type mismatch without executing the statements.

## Type system
The type system contains basic types:
- `Unit` - a constant representing a single instance (kotlin Unit) - you can think about it as void
- `Int` - a classifier for whole number (kotlin Int) 
- `Double` - a classifier for floating point number (kotlin Double) 
- `String` - a string classifier (kotlin String)
- `Bool` - a bool classifier (kotlin Boolean)
- `Tuple` - a tuple classifier, that contains exactly N values of any of the types.
- `Function` - a function classifier. Has a list of argument types (majority of function signature), return type and a beginning statement/node.
    
    
## Syntax
### Expressions
Expression is a node that has a concrete runtime value associated. The expression could be an
instance of any of the above types, but also:
- **Unary/Binary/Ternary Expression** - referred collectively as N-ary expressions
- **Function Call**
- **Variable declaration**
- **Variable reference**

First we will talk about variables and N-nary expressions.
### Variables
To declare a variable, simply write

```
val a = <expression>
```

The type of the expression will be inferred by the type checker. The assignment is 
represented as a single node of type Unit. In particular expression could be a function
definition (which creates a function).

### Operators
#### Ternary Operators
Every N-ary expression has an operator which controls the behavior of the expression.
For example a Ternary expression has only one operator - **if** (`IF`), which has a condition of
bool type and two branches. Two branches must return the same type. For example we don't allow:

```
val a = if (<some condition>) 
            "4" + "4"
        else
            4 
```

because type of first branch is a string and type of the second branch is int. 
**For every if statement, we require there to be an else branch.**
For example:

```
...
if (<some condition>)
    val b = 4
else
    UNIT
...
```

Is a correct use of the if expression. We will get into why UNIT is used in `Variables` section.
#### Unary Operators
Same as with ternary operators, there is only one unary operator - **~** (`NOT`). It negates value of the
argument. Negation could be differently defined for types. You can negate:

- **Bool** - results in negation of the parameter 
- **Int** - results in negative int 
- **Double** - results in negative double 


#### Binary operators
The largest set of operators in MFPL are the binary operator, which have two arguments and a specific rules of resolving types.
The operators are used in infix notation.
- **+** (`PLUS`) operator for strings - it creates a new string constant, that is concatenation of left and right argument
- **+** (`PLUS`), **-** (`MINUS`), **\*** (`MULTIPLY`), **/** (`DIVIDE`) arithmetic operators - they create a new number (int or double) based on the type of input arguments. **The arguments must be of the same type** - we cannot
  add an int to a double. The only accepted types of arguments are int and double.
- **==** (`EQ`), **!=** (`NEQ`), **<=** (`LTE`), **>=** (`GTE`), **<** (`LT`), **>** (`GT`) comparison operators - They compare the values of left and right parameters and return a bool constant.
  The type of the arguments must be the same to be compared. The comparison semantics are described below.
- **&&** (`AND`), **||** (`OR`) logic operators - They expect both arguments to be of type bool and return a bool.

#### Comparisons
Not all types can be compared with eachother. The comparable types are:
- `Bool` (same as numbers, when `true` is represented as `1`, `false` is `0`)
- `Int`
- `Double`
- `String` (alphabetical ordering)

#### Operator precedence.
The higher an operator is on the list below, the higher precedence it has:
1. `IF` 
2. `NOT`
3. `MULTIPLY`, `DIVIDE`
4. `PLUS`, `MINUS`
5. **Comparison operators**
6. `AND`
7. `OR`

Example with operators

```
val a = 1 + 2 * 3
val b = 1 * 2 + 3
if (a == 5 || ~(b == 1))
    ...
else
    ... // this branch gets executed
```
 
## Name resolving
Every node of the program exists in an environment that resolves references to variables. For example when you declare a global variable

```
val a = 3
```

Then this tells the executing object, that it should declare a variable in it's environment. Now when we reference the variable:

```
a + 3
```

The executing object has the variable `a` in environment and can resolve it's type/value.

Each function also has it's own environment. When setting a value, the function declares value only at it's environment. When getting a value,
the function may find it in it's environment and then fetch it. When it is not found, then the enclosing environment is checked for value with
given name. This goes all the way to the global environment. If the variable of given name is not found, then an error in the form of exception
or invalid type is thrown.

Let's consider more complex example:

```
val a = 5
val b = 3
val c = 3
fun b(a: Int) -> Int = {
    val d = 3
    a + c
} 
```

In this example we declare three variables in the global environment - `a`, `b` and `c`. The `b` value of variable is overridden by 
a value of `function` object, that declares a variable `a` in its environment. 
Then during checking/executing the function, the variable `d` is put into the environment of `b` function.
Finally the returned value references the `a` value of the functions environment. Since the `c` value is not in the
functions environment, we look up the variable in enclosing environment. We resolve its value to be 3 and plug it into the
return value expression.

As an interesting quirk of name resolving, consider:

```
val a = if (<some condition>) {
    val a = 3
    2
} else 
    3
```

What is the value of `a`?

## Functions
A function is an object that accepts parameters (of the given argument types) and returns a value (of the return type). A function is declared using curly
braces `{}`, for example:

```
(b: Int) -> Int {
    val a = 4
    if (a == 3)
        b
    else
        5
}
```

This declares an anonymous function (a function without a name) that will execute the statements in between `{}` - the function body. 
The function arguments could be specified in `()` in a comma separated list with types specified after **"<argument name>: "**. The return type
of the function is specified after `->` and is an Int.
The type of this expression is expressed by a function type (it's signature) - `(Int) -> Int`
The last expression in the function is the return value - in this case an IF expression with type of the return type (Int).

We can assign function to a variable (label), as the definition is considered a valid value.

```
val a = (<args>) -> <return type> {
    ...
} 
```

or use the `fun` keyword

```
fun a(<args>) -> <return type> {
    ...
}
```

Both versions are equivalent, the second is syntactic sugar.

### Function calls
To call a function we must use its associated variable name and specify the arguments in `()`:

```
fun a(b: Int, c: String) -> Unit {
    ...
}

a(1, "2")
```

The type of this function is `(Int) -> ((String) -> Unit)`
When calling a function we can either specify:
- All parameters - the returned value is of return type
- Some parameters - the returned value is of a function type determined by parametes not specified in the call

For example the type of expression:

```
a(1)
```

is `(String) -> Int`. Notice the passed argument "trimmed" an Int off of the function signature. This mechanism is called currying.

Notice, that function semantics and syntax was heavily inspired by Ocaml. 

## Tuples and pattern matching
[WORK IN PROGRESS]

## Code optimizations
There are currently no code optimizations mechanisms like constant folding or branch collapse. I plan to implement them in the future.

## A brief note on functions
The functions are implemented by using a FunctionNode. A FunctionNode is a node, that holds a `current` node, which is equivalent to an inner function instruction, and a `next` node which is the next inner function instruction. It is basically what you call a _sequence_ of code.
For example the function:

```
fun f() -> Int = {
        val a = 5
        val b = a + 1
        b
    }
```

Could be written as (Omitting function declaration representation and plus operator for readability):

```
val f = FunctionNode(
    [], Type.INT,
    DeclareVariable("a", 5),
    FunctionNode(
        DeclareVariable("b", VariableReference("a") + IntegerConstant(1)),
        VariableReference("b")
    )
)
```

We are using a `FunctionNode` as a next node, so we can inject the next statement into the function body, therefore a function is a literal linked list of instructions!

Note, the `current` node might also be a function node and I am looking forward to explore this quirk.

## Repository notes
Feel free to fork or commit to this repository. For now I want to implement lexer and parser myself, but any contributions are welcome. I also plan to publish the code under open source license so everyone can use and modify it freely.
The mascot for the language is yet to be chosen...


