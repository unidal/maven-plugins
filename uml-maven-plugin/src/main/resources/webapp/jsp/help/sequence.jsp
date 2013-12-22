<%@ page contentType="text/html; charset=utf-8"%>
<%@ taglib prefix="a" uri="/WEB-INF/app.tld"%>

<a:layout>

<br>

<div>
<h2>Basic examples</h2>
<pre>@startuml
Alice -&gt; Bob: Authentication Request
Bob --&gt; Alice: Authentication Response

Alice -&gt; Bob: Another authentication Request
Alice &lt;-- Bob: another authentication Response
@enduml</pre>
<img src='/uml/help/sequence/01-Basic+examples.uml'>
</div>

<div>
<h2>Declaring participant</h2>
<pre>@startuml
actor Foo1
boundary Foo2
control Foo3
entity Foo4
database Foo5
Foo1 -&gt; Foo2 : To boundary
Foo1 -&gt; Foo3 : To control
Foo1 -&gt; Foo4 : To entity
Foo1 -&gt; Foo5 : To database

@enduml</pre>
<img src='/uml/help/sequence/03-Declaring+participant.uml'>
</div>

<div>
<h2>Use non-letters in participants</h2>
<pre>@startuml
actor Bob #red
' The only difference between actor and participant is the drawing
participant Alice
participant &quot;I have a really\nlong name&quot; as L #99FF99
/' You can also declare:
   participant L as &quot;I have a really\nlong name&quot;  #99FF99
  '/

Alice-&gt;Bob: Authentication Request
Bob-&gt;Alice: Authentication Response
Bob-&gt;L: Log transaction
@enduml</pre>
<img src='/uml/help/sequence/04-Use+non-letters+in+participants.uml'>
</div>

<div>
<h2>Message to Self</h2>
<pre>@startuml
Alice -&gt; &quot;Bob()&quot; : Hello
&quot;Bob()&quot; -&gt; &quot;This is very\nlong&quot; as Long
' You can also declare:
' &quot;Bob()&quot; -&gt; Long as &quot;This is very\nlong&quot;
Long --&gt; &quot;Bob()&quot; : ok
@enduml</pre>
<img src='/uml/help/sequence/05-Message+to+Self.uml'>
</div>

<div>
<h2>Change arrow style</h2>
<pre>@startuml
Alice-&gt;Alice: This is a signal to self.\nIt also demonstrates\nmultiline \ntext
@enduml</pre>
<img src='/uml/help/sequence/06-Change+arrow+style.uml'>
</div>

<div>
<h2>Change arrow color</h2>
<pre>@startuml
Bob -&gt; Alice
Bob -&gt;&gt; Alice
Bob -\ Alice
Bob \\- Alice
Bob //-- Alice

Bob -&gt;o Alice
Bob o\\-- Alice

Bob &lt;-&gt; Alice
Bob &lt;-&gt;o Alice
@enduml</pre>
<img src='/uml/help/sequence/07-Change+arrow+color.uml'>
</div>

<div>
<h2>Message sequence numbering</h2>
<pre>@startuml
Bob -[#red]&gt; Alice : hello
Alice -[#0000FF]-&gt;Bob : ok
@enduml</pre>
<img src='/uml/help/sequence/08-Message+sequence+numbering.uml'>
</div>

<div>
<h2>Title</h2>
<pre>@startuml
autonumber &quot;&lt;b&gt;[000]&quot;
Bob -&gt; Alice : Authentication Request
Bob &lt;- Alice : Authentication Response

autonumber 15 &quot;&lt;b&gt;(&lt;u&gt;##&lt;/u&gt;)&quot;
Bob -&gt; Alice : Another authentication Request
Bob &lt;- Alice : Another authentication Response

autonumber 40 10 &quot;&lt;font color=red&gt;&lt;b&gt;Message 0  &quot;
Bob -&gt; Alice : Yet another authentication Request
Bob &lt;- Alice : Yet another authentication Response

@enduml</pre>
<img src='/uml/help/sequence/09-Title.uml'>
</div>

<div>
<h2>Legend the diagram</h2>
<pre>@startuml

title Simple communication example

Alice -&gt; Bob: Authentication Request
Bob --&gt; Alice: Authentication Response

@enduml</pre>
<img src='/uml/help/sequence/10-Legend+the+diagram.uml'>
</div>

<div>
<h2>Splitting diagrams</h2>
<pre>@startuml

Alice -&gt; Bob : Hello
legend right
  Short
  legend
endlegend

@enduml</pre>
<img src='/uml/help/sequence/11-Splitting+diagrams.uml'>
</div>

<div>
<h2>Grouping message</h2>
<pre>@startuml

Alice -&gt; Bob : message 1
Alice -&gt; Bob : message 2

newpage

Alice -&gt; Bob : message 3
Alice -&gt; Bob : message 4

newpage A title for the\nlast page

Alice -&gt; Bob : message 5
Alice -&gt; Bob : message 6
@enduml</pre>
<img src='/uml/help/sequence/12-Grouping+message.uml'>
</div>

<div>
<h2>Notes on messages</h2>
<pre>@startuml
Alice -&gt; Bob: Authentication Request

alt successful case

    Bob -&gt; Alice: Authentication Accepted
    
else some kind of failure

    Bob -&gt; Alice: Authentication Failure
    group My own label
    	Alice -&gt; Log : Log attack start
        loop 1000 times
            Alice -&gt; Bob: DNS Attack
        end
    	Alice -&gt; Log : Log attack end
    end
    
else Another type of failure

   Bob -&gt; Alice: Please repeat
   
end
@enduml</pre>
<img src='/uml/help/sequence/13-Notes+on+messages.uml'>
</div>

<div>
<h2>Some other notes</h2>
<pre>@startuml
Alice-&gt;Bob : hello
note left: this is a first note

Bob-&gt;Alice : ok
note right: this is another note

Bob-&gt;Bob : I am thinking
note left
	a note
	can also be defined
	on several lines
end note
@enduml</pre>
<img src='/uml/help/sequence/14-Some+other+notes.uml'>
</div>

<div>
<h2>Formatting using HTML</h2>
<pre>@startuml
participant Alice
participant Bob
note left of Alice #aqua
	This is displayed 
	left of Alice. 
end note
 
note right of Alice: This is displayed right of Alice.

note over Alice: This is displayed over Alice.

note over Alice, Bob #FFAAAA: This is displayed\n over Bob and Alice.

note over Bob, Alice
	This is yet another
	example of
	a long note.
end note
@enduml</pre>
<img src='/uml/help/sequence/15-Formatting+using+HTML.uml'>
</div>

<div>
<h2>Divider</h2>
<pre>@startuml
participant Alice
participant &quot;The &lt;b&gt;Famous&lt;/b&gt; Bob&quot; as Bob

Alice -&gt; Bob : A &lt;i&gt;well formated&lt;/i&gt; message
note right of Alice 
 This is &lt;back:cadetblue&gt;&lt;size:18&gt;displayed&lt;/size&gt;&lt;/back&gt; 
 &lt;u&gt;left of&lt;/u&gt; Alice. 
end note
note left of Bob 
 &lt;u:red&gt;This&lt;/u&gt; is &lt;color #118888&gt;displayed&lt;/color&gt; 
 &lt;b&gt;&lt;color purple&gt;left of&lt;/color&gt; &lt;s:red&gt;Alice&lt;/strike&gt; Bob&lt;/b&gt;. 
end note
note over Alice, Bob
 &lt;w:#FF33FF&gt;This is hosted&lt;/w&gt; by &lt;img sourceforge.jpg&gt;
end note
 
@enduml</pre>
<img src='/uml/help/sequence/16-Divider.uml'>
</div>

<div>
<h2>Reference</h2>
<pre>@startuml

== Initialisation ==

Alice -&gt; Bob: Authentication Request
Bob --&gt; Alice: Authentication Response

== Repetition ==

Alice -&gt; Bob: Another authentication Request
Alice &lt;-- Bob: another authentication Response

@enduml</pre>
<img src='/uml/help/sequence/17-Reference.uml'>
</div>

<div>
<h2>Delay</h2>
<pre>@startuml
participant Alice
actor Bob

ref over Alice, Bob : init

Alice -&gt; Bob : hello

ref over Bob
  This can be on
  several lines
end ref
@enduml</pre>
<img src='/uml/help/sequence/18-Delay.uml'>
</div>

<div>
<h2>Space</h2>
<pre>@startuml

Alice -&gt; Bob: Authentication Request
...
Bob --&gt; Alice: Authentication Response
...5 minutes latter...
Bob --&gt; Alice: Bye !

@enduml</pre>
<img src='/uml/help/sequence/19-Space.uml'>
</div>

<div>
<h2>Lifeline Activation and Destruction</h2>
<pre>@startuml

Alice -&gt; Bob: message 1
Bob --&gt; Alice: ok
|||
Alice -&gt; Bob: message 2
Bob --&gt; Alice: ok
||45||
Alice -&gt; Bob: message 3
Bob --&gt; Alice: ok

@enduml</pre>
<img src='/uml/help/sequence/20-Lifeline+Activation+and+Destruction.uml'>
</div>

<div>
<h2>Participant creation</h2>
<pre>@startuml
participant User

User -&gt; A: DoWork
activate A #FFBBBB

A -&gt; A: Internal call
activate A #DarkSalmon

A -&gt; B: &lt;&lt; createRequest &gt;&gt;
activate B

B --&gt; A: RequestCreated
deactivate B
deactivate A
A -&gt; User: Done
deactivate A

@enduml</pre>
<img src='/uml/help/sequence/21-Participant+creation.uml'>
</div>

<div>
<h2>Incoming and outgoing messages</h2>
<pre>@startuml
Bob -&gt; Alice : hello

create Other
Alice -&gt; Other : new

create control String
Alice -&gt; String
note right : You can also put notes!

Alice --&gt; Bob : ok

@enduml</pre>
<img src='/uml/help/sequence/22-Incoming+and+outgoing+messages.uml'>
</div>

<div>
<h2>Stereotypes and Spots</h2>
<pre>@startuml
[-&gt; A: DoWork

activate A

A -&gt; A: Internal call
activate A

A -&gt;] : &lt;&lt; createRequest &gt;&gt;

A&lt;--] : RequestCreated
deactivate A
[&lt;- A: Done
deactivate A
@enduml</pre>
<img src='/uml/help/sequence/23-Stereotypes+and+Spots.uml'>
</div>

<div>
<h2>More information on titles</h2>
<pre>@startuml

participant Bob &lt;&lt; (C,#ADD1B2) &gt;&gt;
participant Alice &lt;&lt; (C,#ADD1B2) &gt;&gt;

Bob-&gt;Alice: First message

@enduml</pre>
<img src='/uml/help/sequence/24-More+information+on+titles.uml'>
</div>

<div>
<h2>Participants englober</h2>
<pre>@startuml

title
 &lt;u&gt;Simple&lt;/u&gt; communication example
 on &lt;i&gt;several&lt;/i&gt; lines and using &lt;font color=red&gt;html&lt;/font&gt;
 This is hosted by &lt;img src=sourceforge.jpg&gt;
end title

Alice -&gt; Bob: Authentication Request
Bob -&gt; Alice: Authentication Response

@enduml</pre>
<img src='/uml/help/sequence/25-Participants+englober.uml'>
</div>

<div>
<h2>Removing Footer</h2>
<pre>@startuml

box &quot;Internal Service&quot; #LightBlue
	participant Bob
	participant Alice
end box
participant Other

Bob -&gt; Alice : hello
Alice -&gt; Other : hello

@enduml</pre>
<img src='/uml/help/sequence/26-Removing+Footer.uml'>
</div>

<div>
<h2>Skinparam</h2>
<pre>@startuml

hide footbox
title Footer removed

Alice -&gt; Bob: Authentication Request
Bob --&gt; Alice: Authentication Response

@enduml</pre>
<img src='/uml/help/sequence/27-Skinparam.uml'>
</div>

<div>
<h2>Skin</h2>
<pre>@startuml
skinparam backgroundColor #EEEBDC

skinparam sequence {
	ArrowColor DeepSkyBlue
	ActorBorderColor DeepSkyBlue
	LifeLineBorderColor blue
	LifeLineBackgroundColor #A9DCDF
	
	ParticipantBorderColor DeepSkyBlue
	ParticipantBackgroundColor DodgerBlue
	ParticipantFontName Impact
	ParticipantFontSize 17
	ParticipantFontColor #A9DCDF
	
	ActorBackgroundColor aqua
	ActorFontColor DeepSkyBlue
	ActorFontSize 17
	ActorFontName Aapex
}

actor User
participant &quot;First Class&quot; as A
participant &quot;Second Class&quot; as B
participant &quot;Last Class&quot; as C

User -&gt; A: DoWork
activate A

A -&gt; B: Create Request
activate B

B -&gt; C: DoWork
activate C
C --&gt; B: WorkDone
destroy C

B --&gt; A: Request Created
deactivate B

A --&gt; User: Done
deactivate A

@enduml</pre>
<img src='/uml/help/sequence/28-Skin.uml'>
</div>



</a:layout>
