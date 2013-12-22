<%@ page contentType="text/html; charset=utf-8"%>
<%@ taglib prefix="a" uri="/WEB-INF/app.tld"%>

<a:layout>

<br>

<div>
<h2>Simple State</h2>
<pre>@startuml

[*] --&gt; State1
State1 --&gt; [*]
State1 : this is a string
State1 : this is another string

State1 -&gt; State2
State2 --&gt; [*]

@enduml</pre>
<img src='/uml/help/state/01-Simple+State.uml'>
</div>

<div>
<h2>Composite state</h2>
<pre>@startuml
scale 350 width
[*] --&gt; NotShooting

state NotShooting {
  [*] --&gt; Idle
  Idle --&gt; Configuring : EvConfig
  Configuring --&gt; Idle : EvConfig
}

state Configuring {
  [*] --&gt; NewValueSelection
  NewValueSelection --&gt; NewValuePreview : EvNewValue
  NewValuePreview --&gt; NewValueSelection : EvNewValueRejected
  NewValuePreview --&gt; NewValueSelection : EvNewValueSaved
  
  state NewValuePreview {
     State1 -&gt; State2
  }
  
}
@enduml</pre>
<img src='/uml/help/state/02-Composite+state.uml'>
</div>

<div>
<h2>Long name</h2>
<pre>@startuml
scale 600 width

[*] -&gt; State1
State1 --&gt; State2 : Succeeded
State1 --&gt; [*] : Aborted
State2 --&gt; State3 : Succeeded
State2 --&gt; [*] : Aborted
state State3 {
  state &quot;Accumulate Enough Data\nLong State Name&quot; as long1
  long1 : Just a test
  [*] --&gt; long1
  long1 --&gt; long1 : New Data
  long1 --&gt; ProcessData : Enough Data
}
State3 --&gt; State3 : Failed
State3 --&gt; [*] : Succeeded / Save Result
State3 --&gt; [*] : Aborted
 
@enduml</pre>
<img src='/uml/help/state/03-Long+name.uml'>
</div>

<div>
<h2>Concurrent state</h2>
<pre>@startuml
scale 800 width

[*] --&gt; Active

state Active {
  [*] -&gt; NumLockOff
  NumLockOff --&gt; NumLockOn : EvNumLockPressed
  NumLockOn --&gt; NumLockOff : EvNumLockPressed
  --
  [*] -&gt; CapsLockOff
  CapsLockOff --&gt; CapsLockOn : EvCapsLockPressed
  CapsLockOn --&gt; CapsLockOff : EvCapsLockPressed
  --
  [*] -&gt; ScrollLockOff
  ScrollLockOff --&gt; ScrollLockOn : EvCapsLockPressed
  ScrollLockOn --&gt; ScrollLockOff : EvCapsLockPressed
} 

@enduml</pre>
<img src='/uml/help/state/04-Concurrent+state.uml'>
</div>

<div>
<h2>Arrow direction</h2>
<pre>@startuml

[*] -up-&gt; First
First -right-&gt; Second
Second --&gt; Third
Third -left-&gt; Last

@enduml</pre>
<img src='/uml/help/state/05-Arrow+direction.uml'>
</div>

<div>
<h2>Note</h2>
<pre>@startuml

[*] --&gt; Active
Active --&gt; Inactive

note left of Active : this is a short\nnote

note right of Inactive
  A note can also
  be defined on
  several lines
end note

@enduml</pre>
<img src='/uml/help/state/06-Note.uml'>
</div>

<div>
<h2>More in notes</h2>
<pre>@startuml

state foo
note &quot;This is a floating note&quot; as N1

@enduml</pre>
<img src='/uml/help/state/07-More+in+notes.uml'>
</div>

<div>
<h2>Skinparam</h2>
<pre>@startuml

[*] --&gt; NotShooting

state &quot;Not Shooting State&quot; as NotShooting {
  state &quot;Idle mode&quot; as Idle
  state &quot;Configuring mode&quot; as Configuring
  [*] --&gt; Idle
  Idle --&gt; Configuring : EvConfig
  Configuring --&gt; Idle : EvConfig
}

note right of NotShooting : This is a note on a composite state

@enduml</pre>
<img src='/uml/help/state/08-Skinparam.uml'>
</div>



</a:layout>
