# Immutability

##What is Immutability

### Mutable
```java
public class Printer{
    private String text;
    
    public void setText(String text){
        this.text=text;
    }

    public void print(){
        System.out.println(text);
    }
    
}
```

### Immutable
```java
public class Printer{
    private final String text;
    
    public Printer(String text){
        this.text=text;
    }

    public void print(){
        System.out.println(text);
    }
    
}
```


##why immutability

* reproducibility
* performance 
  * parallel computing
  * no io
* no shared state (popular oop critic) https://www.youtube.com/watch?v=QM1iUe6IofM&t=1205s

##The problem

real world application need mutable data. How make a immutable application mutable?  
=> Recreate all  
![picture1](picture1.png)
=> optimize recreation
![picture2](picture2.png)
=> Factoryfx implementation
![picture3](picture3.png)

##The factory Layer
* no merge/compare code in liveobjects
* no dependencies for liveobjects

##Update process
![picture3](picture4.png)