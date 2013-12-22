<%@ page contentType="text/html; charset=utf-8"%>
<%@ taglib prefix="a" uri="/WEB-INF/app.tld"%>

<a:layout>

<br>

<div>
<h2>Components</h2>
<pre>@startuml

[First component]
[Another component] as Comp2  
component Comp3
component [Last\ncomponent] as Comp4

@enduml</pre>
<img src='/uml/help/component/01-Components.uml'>
</div>

<div>
<h2>Interfaces</h2>
<pre>@startuml

() &quot;First Interface&quot;
() &quot;Another interface&quot; as Interf2
interface Interf3
interface &quot;Last\ninterface&quot; as Interf4

@enduml</pre>
<img src='/uml/help/component/02-Interfaces.uml'>
</div>

<div>
<h2>Basic example</h2>
<pre>@startuml

DataAccess - [First Component] 
[First Component] ..&gt; HTTP : use

@enduml</pre>
<img src='/uml/help/component/03-Basic+example.uml'>
</div>

<div>
<h2>Using notes</h2>
<pre>@startuml

interface &quot;Data Access&quot; as DA

DA - [First Component] 
[First Component] ..&gt; HTTP : use

note left of HTTP : Web Service only

note right of [First Component]
  A note can also
  be on several lines
end note

@enduml</pre>
<img src='/uml/help/component/04-Using+notes.uml'>
</div>

<div>
<h2>Grouping Components</h2>
<pre>@startuml

package &quot;Some Group&quot; {
  HTTP - [First Component]
  [Another Component]
}
 
node &quot;Other Groups&quot; {
  FTP - [Second Component]
  [First Component] --&gt; FTP
} 

cloud {
  [Example 1]
}


database &quot;MySql&quot; {
  folder &quot;This is my folder&quot; {
    [Folder 3]
  }
  frame &quot;Foo&quot; {
    [Frame 4]
  }
}


[Another Component] --&gt; [Example 1]
[Example 1] --&gt; [Folder 3]
[Folder 3] --&gt; [Frame 4]

@enduml</pre>
<img src='/uml/help/component/05-Grouping+Components.uml'>
</div>

<div>
<h2>Changing arrows direction</h2>
<pre>@startuml
[Component] --&gt; Interface1
[Component] -&gt; Interface2
@enduml</pre>
<img src='/uml/help/component/06-Changing+arrows+direction.uml'>
</div>

<div>
<h2>Title the diagram</h2>
<pre>@startuml
[Component] -left-&gt; left 
[Component] -right-&gt; right 
[Component] -up-&gt; up
[Component] -down-&gt; down
@enduml</pre>
<img src='/uml/help/component/07-Title+the+diagram.uml'>
</div>

<div>
<h2>Use UML2 notation</h2>
<pre>@startuml
title Very simple component\ndiagram

interface &quot;Data Access&quot; as DA

DA - [First Component] 
[First Component] ..&gt; HTTP : use

@enduml</pre>
<img src='/uml/help/component/08-Use+UML2+notation.uml'>
</div>

<div>
<h2>Skinparam</h2>
<pre>@startuml
skinparam componentStyle uml2

interface &quot;Data Access&quot; as DA

DA - [First Component] 
[First Component] ..&gt; HTTP : use

@enduml</pre>
<img src='/uml/help/component/09-Skinparam.uml'>
</div>



</a:layout>
