<%@ page contentType="text/html; charset=utf-8"%>
<%@ taglib prefix="a" uri="/WEB-INF/app.tld"%>

<a:layout>

<br>

<div>
<h2>Relations between classes</h2>
<pre>@startuml
scale 900 width
Class01 &lt;|-- Class02
Class03 *-- Class04
Class05 o-- Class06
Class07 .. Class08
Class09 -- Class10
Class11 &lt;|.. Class12
Class13 --&gt; Class14
Class15 ..&gt; Class16
Class17 ..|&gt; Class18
Class19 &lt;--* Class20
@enduml</pre>
<img src='/uml/help/classes/02-Relations+between+classes.uml'>
</div>

<div>
<h2>Label on relations</h2>
<pre>@startuml

Class01 &quot;1&quot; *-- &quot;many&quot; Class02 : contains

Class03 o-- Class04 : agregation

Class05 --&gt; &quot;1&quot; Class06

@enduml</pre>
<img src='/uml/help/classes/03-Label+on+relations.uml'>
</div>

<div>
<h2>Adding methods</h2>
<pre>@startuml
class Car

Driver - Car : drives &gt;
Car *- Wheel : have 4 &gt;
Car -- Person : &lt; owns

@enduml</pre>
<img src='/uml/help/classes/04-Adding+methods.uml'>
</div>

<div>
<h2>Defining visibility</h2>
<pre>@startuml
class Dummy {
  String data
  void methods()
}

class Flight {
   flightNumber : Integer
   departureTime : Date
}
@enduml</pre>
<img src='/uml/help/classes/05-Defining+visibility.uml'>
</div>

<div>
<h2>Abstract and Static</h2>
<pre>@startuml
skinparam classAttributeIconSize 0
class Dummy {
 -field1
 #field2
 ~method1()
 +method2()
}

@enduml</pre>
<img src='/uml/help/classes/06-Abstract+and+Static.uml'>
</div>

<div>
<h2>Advanced class body</h2>
<pre>@startuml
class Dummy {
  {static} String id
  {abstract} void methods()
}
@enduml</pre>
<img src='/uml/help/classes/07-Advanced+class+body.uml'>
</div>

<div>
<h2>Notes and stereotypes</h2>
<pre>@startuml
class Foo1 {
  You can use
  several lines
  ..
  as you want
  and group
  ==
  things together.
  __
  You can have as many groups
  as you want
  --
  End of class
}

class User {
  .. Simple Getter ..
  + getName()
  + getAddress()
  .. Some setter ..
  + setName()
  __ private data __
  int age
  -- crypted --
  String password
}

@enduml</pre>
<img src='/uml/help/classes/08-Notes+and+stereotypes.uml'>
</div>

<div>
<h2>More on notes</h2>
<pre>@startuml
class Object &lt;&lt; general &gt;&gt;
Object &lt;|--- ArrayList

note top of Object : In java, every class\nextends this one.

note &quot;This is a floating note&quot; as N1
note &quot;This note is connected\nto several objects.&quot; as N2
Object .. N2
N2 .. ArrayList

class Foo
note left: On last defined class

@enduml</pre>
<img src='/uml/help/classes/09-More+on+notes.uml'>
</div>

<div>
<h2>Note on links</h2>
<pre>@startuml

class Foo
note left: On last defined class

note top of Object
  In java, &lt;size:18&gt;every&lt;/size&gt; &lt;u&gt;class&lt;/u&gt;
  &lt;b&gt;extends&lt;/b&gt;
  &lt;i&gt;this&lt;/i&gt; one.
end note

note as N1
  This note is &lt;u&gt;also&lt;/u&gt;
  &lt;b&gt;&lt;color:royalBlue&gt;on several&lt;/color&gt;
  &lt;s&gt;words&lt;/s&gt; lines
  And this is hosted by &lt;img:sourceforge.jpg&gt;
end note

@enduml</pre>
<img src='/uml/help/classes/10-Note+on+links.uml'>
</div>

<div>
<h2>Abstract class and interface</h2>
<pre>@startuml

class Dummy
Dummy --&gt; Foo : A link
note on link #red: note that is red

Dummy --&gt; Foo2 : Another link
note right on link #blue
	this is my note on right link
	and in blue
end note

@enduml</pre>
<img src='/uml/help/classes/11-Abstract+class+and+interface.uml'>
</div>

<div>
<h2>Using non-letters</h2>
<pre>@startuml

abstract class AbstractList
abstract AbstractCollection
interface List
interface Collection

List &lt;|-- AbstractList
Collection &lt;|-- AbstractCollection

Collection &lt;|- List
AbstractCollection &lt;|- AbstractList
AbstractList &lt;|-- ArrayList

class ArrayList {
  Object[] elementData
  size()
}

enum TimeUnit {
  DAYS
  HOURS
  MINUTES
}

annotation SuppressWarnings

@enduml</pre>
<img src='/uml/help/classes/12-Using+non-letters.uml'>
</div>

<div>
<h2>Hide attributes, methods...</h2>
<pre>@startuml
class &quot;This is my class&quot; as class1
class class2 as &quot;It works this way too&quot;

class2 *-- &quot;foo/dummy&quot; : use
@enduml</pre>
<img src='/uml/help/classes/13-Hide+attributes,+methods....uml'>
</div>

<div>
<h2>Hide classes</h2>
<pre>@startuml

class Dummy1 {
  +myMethods()
}

class Dummy2 {
  +hiddenMethod()
}

class Dummy3 &lt;&lt;Serializable&gt;&gt; {
	String name
}

hide members
hide &lt;&lt;Serializable&gt;&gt; circle
show Dummy1 methods
show &lt;&lt;Serializable&gt;&gt; fields

@enduml</pre>
<img src='/uml/help/classes/14-Hide+classes.uml'>
</div>

<div>
<h2>Use generics</h2>
<pre>@startuml

class Foo1
class Foo2

Foo2 *-- Foo1

hide Foo2

@enduml</pre>
<img src='/uml/help/classes/15-Use+generics.uml'>
</div>

<div>
<h2>Specific Spot</h2>
<pre>@startuml

class Foo&lt;? extends Element&gt; {
  int size()
}
Foo *- Element

@enduml</pre>
<img src='/uml/help/classes/16-Specific+Spot.uml'>
</div>

<div>
<h2>Packages</h2>
<pre>@startuml

class System &lt;&lt; (S,#FF7700) Singleton &gt;&gt;
class Date &lt;&lt; (D,orchid) &gt;&gt;
@enduml</pre>
<img src='/uml/help/classes/17-Packages.uml'>
</div>

<div>
<h2>Packages style</h2>
<pre>@startuml

package &quot;Classic Collections&quot; #DDDDDD {
  Object &lt;|-- ArrayList
}

package net.sourceforge.plantuml
  Object &lt;|-- Demo1
  Demo1 *- Demo2
end package

@enduml</pre>
<img src='/uml/help/classes/18-Packages+style.uml'>
</div>

<div>
<h2>Namespaces</h2>
<pre>@startuml

skinparam packageStyle rect

package foo1.foo2
end package

package foo1.foo2.foo3 {
  class Object
}

foo1.foo2 +-- foo1.foo2.foo3

@enduml</pre>
<img src='/uml/help/classes/19-Namespaces.uml'>
</div>

<div>
<h2>Automatic namespace creation</h2>
<pre>@startuml

class BaseClass

namespace net.dummy #DDDDDD
    .BaseClass &lt;|-- Person
    Meeting o-- Person
    
    .BaseClass &lt;|- Meeting

end namespace

namespace net.foo {
  net.dummy.Person  &lt;|- Person
  .BaseClass &lt;|-- Person

  net.dummy.Meeting o-- Person
}

BaseClass &lt;|-- net.unused.Person

@enduml</pre>
<img src='/uml/help/classes/20-Automatic+namespace+creation.uml'>
</div>

<div>
<h2>Lollipop interface</h2>
<pre>@startuml

set namespaceSeparator none
class X1.X2.foo {
  some info
}

@enduml</pre>
<img src='/uml/help/classes/21-Lollipop+interface.uml'>
</div>

<div>
<h2>Changing arrows direction</h2>
<pre>@startuml
class foo
bar ()- foo
@enduml</pre>
<img src='/uml/help/classes/22-Changing+arrows+direction.uml'>
</div>

<div>
<h2>Title the diagram</h2>
<pre>@startuml
foo -left-&gt; dummyLeft 
foo -right-&gt; dummyRight 
foo -up-&gt; dummyUp 
foo -down-&gt; dummyDown
@enduml</pre>
<img src='/uml/help/classes/23-Title+the+diagram.uml'>
</div>

<div>
<h2>Legend the diagram</h2>
<pre>@startuml

title Simple &lt;b&gt;example&lt;/b&gt;\nof title 
Object &lt;|-- ArrayList

@enduml</pre>
<img src='/uml/help/classes/24-Legend+the+diagram.uml'>
</div>

<div>
<h2>Association classes</h2>
<pre>@startuml

Object &lt;|- ArrayList

legend right
  &lt;b&gt;Object&lt;/b&gt; and &lt;b&gt;ArrayList&lt;/b&gt;
  are simple class
endlegend

@enduml</pre>
<img src='/uml/help/classes/25-Association+classes.uml'>
</div>

<div>
<h2>Skinparam</h2>
<pre>@startuml
class Student {
  Name
}
Student &quot;0..*&quot; -- &quot;1..*&quot; Course
(Student, Course) . Enrollment

class Enrollment {
  drop()
  cancel()
}
@enduml</pre>
<img src='/uml/help/classes/26-Skinparam.uml'>
</div>

<div>
<h2>Skinned Stereotypes</h2>
<pre>@startuml

skinparam class {
	BackgroundColor PaleGreen
	ArrowColor SeaGreen
	BorderColor SpringGreen
}
skinparam stereotypeCBackgroundColor YellowGreen

Class01 &quot;1&quot; *-- &quot;many&quot; Class02 : contains

Class03 o-- Class04 : agregation

@enduml</pre>
<img src='/uml/help/classes/27-Skinned+Stereotypes.uml'>
</div>

<div>
<h2>Color gradient</h2>
<pre>@startuml

skinparam class {
	BackgroundColor PaleGreen
	ArrowColor SeaGreen
	BorderColor SpringGreen
	BackgroundColor&lt;&lt;Foo&gt;&gt; Wheat
	BorderColor&lt;&lt;Foo&gt;&gt; Tomato
}
skinparam stereotypeCBackgroundColor YellowGreen
skinparam stereotypeCBackgroundColor&lt;&lt; Foo &gt;&gt; DimGray

Class01 &lt;&lt; Foo &gt;&gt;
Class01 &quot;1&quot; *-- &quot;many&quot; Class02 : contains

Class03&lt;&lt;Foo&gt;&gt; o-- Class04 : agregation

@enduml</pre>
<img src='/uml/help/classes/28-Color+gradient.uml'>
</div>

<div>
<h2>Splitting large files</h2>
<pre>@startuml

skinparam backgroundcolor AntiqueWhite/Gold
skinparam classBackgroundColor Wheat|CornflowerBlue

class Foo #red-green
note left of Foo #blue\9932CC {
  this is my
  note on this class
}

package example #GreenYellow/LightGoldenRodYellow {
  class Dummy
}

@enduml</pre>
<img src='/uml/help/classes/29-Splitting+large+files.uml'>
</div>



</a:layout>
