<%@ page contentType="text/html; charset=utf-8"%>
<%@ taglib prefix="a" uri="/WEB-INF/app.tld"%>

<a:layout>

<br>

<div>
<h2>Usecases</h2>
<pre>@startuml

(First usecase)
(Another usecase) as (UC2)  
usecase UC3
usecase (Last\nusecase) as UC4

@enduml</pre>
<img src='/uml/help/usecase/01-Usecases.uml'>
</div>

<div>
<h2>Actors</h2>
<pre>@startuml

:First Actor:
:Another\nactor: as Men2  
actor Men3
actor :Last actor: as Men4

@enduml</pre>
<img src='/uml/help/usecase/02-Actors.uml'>
</div>

<div>
<h2>Usecases description</h2>
<pre>@startuml

usecase UC1 as &quot;You can use
several lines to define your usecase.
You can also use separators.
--
Several separators are possible.
==
And you can add titles:
..Conclusion..
This allows large description.&quot;

@enduml</pre>
<img src='/uml/help/usecase/03-Usecases+description.uml'>
</div>

<div>
<h2>Basic example</h2>
<pre>@startuml

User -&gt; (Start)
User --&gt; (Use the application) : A small label

:Main Admin: ---&gt; (Use the application) : This is\nyet another\nlabel

@enduml</pre>
<img src='/uml/help/usecase/04-Basic+example.uml'>
</div>

<div>
<h2>Extension</h2>
<pre>@startuml
:Main Admin: as Admin
(Use the application) as (Use)

User &lt;|-- Admin
(Start) &lt;|-- (Use)

@enduml</pre>
<img src='/uml/help/usecase/05-Extension.uml'>
</div>

<div>
<h2>Using notes</h2>
<pre>@startuml
:Main Admin: as Admin
(Use the application) as (Use)

User -&gt; (Start)
User --&gt; (Use)

Admin ---&gt; (Use)

note right of Admin : This is an example.

note right of (Use)
  A note can also
  be on several lines
end note

note &quot;This note is connected\nto several objects.&quot; as N2
(Start) .. N2
N2 .. (Use)
@enduml</pre>
<img src='/uml/help/usecase/06-Using+notes.uml'>
</div>

<div>
<h2>Stereotypes</h2>
<pre>@startuml
User &lt;&lt; Human &gt;&gt;
:Main Database: as MySql &lt;&lt; Application &gt;&gt;
(Start) &lt;&lt; One Shot &gt;&gt;
(Use the application) as (Use) &lt;&lt; Main &gt;&gt;

User -&gt; (Start)
User --&gt; (Use)

MySql --&gt; (Use)

@enduml</pre>
<img src='/uml/help/usecase/07-Stereotypes.uml'>
</div>

<div>
<h2>Changing arrows direction</h2>
<pre>@startuml
:user: --&gt; (Use case 1)
:user: -&gt; (Use case 2)
@enduml</pre>
<img src='/uml/help/usecase/08-Changing+arrows+direction.uml'>
</div>

<div>
<h2>Title the diagram</h2>
<pre>@startuml
:user: -left-&gt; (dummyLeft) 
:user: -right-&gt; (dummyRight) 
:user: -up-&gt; (dummyUp)
:user: -down-&gt; (dummyDown)
@enduml</pre>
<img src='/uml/help/usecase/09-Title+the+diagram.uml'>
</div>

<div>
<h2>Splitting diagrams</h2>
<pre>@startuml
title Simple &lt;b&gt;Usecase&lt;/b&gt;\nwith one actor

&quot;Use the application&quot; as (Use)
User -&gt; (Use)

@enduml</pre>
<img src='/uml/help/usecase/10-Splitting+diagrams.uml'>
</div>

<div>
<h2>Left to right direction</h2>
<pre>@startuml
:actor1: --&gt; (Usecase1)
newpage
:actor2: --&gt; (Usecase2)
@enduml</pre>
<img src='/uml/help/usecase/11-Left+to+right+direction.uml'>
</div>

<div>
<h2>Skinparam</h2>
<pre>@startuml

left to right direction
user1 --&gt; (Usecase 1)
user2 --&gt; (Usecase 2)

@enduml</pre>
<img src='/uml/help/usecase/12-Skinparam.uml'>
</div>

<div>
<h2>Complete example</h2>
<pre>@startuml

skinparam usecase {
	BackgroundColor DarkSeaGreen
	BorderColor DarkSlateGray

	BackgroundColor&lt;&lt; Main &gt;&gt; YellowGreen
	BorderColor&lt;&lt; Main &gt;&gt; YellowGreen
	
	ArrowColor Olive
	ActorBorderColor black
	ActorFontName Courier

	ActorBackgroundColor&lt;&lt; Human &gt;&gt; Gold
}

User &lt;&lt; Human &gt;&gt;
:Main Database: as MySql &lt;&lt; Application &gt;&gt;
(Start) &lt;&lt; One Shot &gt;&gt;
(Use the application) as (Use) &lt;&lt; Main &gt;&gt;

User -&gt; (Start)
User --&gt; (Use)

MySql --&gt; (Use)

@enduml</pre>
<img src='/uml/help/usecase/13-Complete+example.uml'>
</div>



</a:layout>
