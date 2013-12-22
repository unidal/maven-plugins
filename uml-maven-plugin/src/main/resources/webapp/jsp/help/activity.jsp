<%@ page contentType="text/html; charset=utf-8"%>
<%@ taglib prefix="a" uri="/WEB-INF/app.tld"%>

<a:layout>

<br>

<div>
<h2>Simple Activity</h2>
<pre>@startuml

(*) --&gt; &quot;First Activity&quot;
&quot;First Activity&quot; --&gt; (*)

@enduml</pre>
<img src='/uml/help/activity/01-Simple+Activity.uml'>
</div>

<div>
<h2>Label on arrows</h2>
<pre>@startuml

(*) --&gt; &quot;First Activity&quot;
--&gt;[You can put also labels] &quot;Second Activity&quot;
--&gt; (*)

@enduml</pre>
<img src='/uml/help/activity/02-Label+on+arrows.uml'>
</div>

<div>
<h2>Changing arrow direction</h2>
<pre>@startuml

(*) -up-&gt; &quot;First Activity&quot;
-right-&gt; &quot;Second Activity&quot;
--&gt; &quot;Third Activity&quot;
-left-&gt; (*)

@enduml</pre>
<img src='/uml/help/activity/03-Changing+arrow+direction.uml'>
</div>

<div>
<h2>Branches</h2>
<pre>@startuml
(*) --&gt; &quot;Initialisation&quot;

if &quot;Some Test&quot; then
  --&gt;[true] &quot;Some Activity&quot;
  --&gt; &quot;Another activity&quot;
  -right-&gt; (*)
else
  -&gt;[false] &quot;Something else&quot;
  --&gt;[Ending process] (*)
endif

@enduml</pre>
<img src='/uml/help/activity/04-Branches.uml'>
</div>

<div>
<h2>More on Branches</h2>
<pre>@startuml
(*)  --&gt; &quot;check input&quot;
If &quot;input is verbose&quot; then
--&gt; [Yes] &quot;turn on verbosity&quot;
--&gt; &quot;run command&quot;
else
--&gt; &quot;run command&quot;
Endif
--&gt;(*)
@enduml</pre>
<img src='/uml/help/activity/05-More+on+Branches.uml'>
</div>

<div>
<h2>Synchronization</h2>
<pre>@startuml

(*) --&gt; if &quot;Some Test&quot; then

  --&gt;[true] &quot;activity 1&quot;
  
  if &quot;&quot; then
    -&gt; &quot;activity 3&quot; as a3
  else
    if &quot;Other test&quot; then
      -left-&gt; &quot;activity 5&quot;
    else
      --&gt; &quot;activity 6&quot;
    endif
  endif
  
else

  -&gt;[false] &quot;activity 2&quot;
  
endif

a3 --&gt; if &quot;last test&quot; then
  --&gt; &quot;activity 7&quot;
else
  -&gt; &quot;activity 8&quot;
endif

@enduml</pre>
<img src='/uml/help/activity/06-Synchronization.uml'>
</div>

<div>
<h2>Long activity description</h2>
<pre>@startuml

(*) --&gt; ===B1=== 
--&gt; &quot;Parallel Activity 1&quot;
--&gt; ===B2===

===B1=== --&gt; &quot;Parallel Activity 2&quot;
--&gt; ===B2===

--&gt; (*)

@enduml</pre>
<img src='/uml/help/activity/07-Long+activity+description.uml'>
</div>

<div>
<h2>Notes</h2>
<pre>@startuml
(*) -left-&gt; &quot;this &lt;size:20&gt;activity&lt;/size&gt;
	is &lt;b&gt;very&lt;/b&gt; &lt;color:red&gt;long2&lt;/color&gt;
	and defined on several lines
	that contains many &lt;i&gt;text&lt;/i&gt;&quot; as A1

-up-&gt; &quot;Another activity\n on several lines&quot;

A1 --&gt; &quot;Short activity &lt;img:sourceforge.jpg&gt;&quot;
@enduml</pre>
<img src='/uml/help/activity/08-Notes.uml'>
</div>

<div>
<h2>Partition</h2>
<pre>@startuml

(*) --&gt; &quot;Some Activity&quot;
note right: This activity has to be defined
&quot;Some Activity&quot; --&gt; (*)
note left
 This note is on
 several lines
end note

@enduml</pre>
<img src='/uml/help/activity/09-Partition.uml'>
</div>

<div>
<h2>Title the diagram</h2>
<pre>@startuml

partition Conductor {
  (*) --&gt; &quot;Climbs on Platform&quot;
  --&gt; === S1 ===
  --&gt; Bows
}

partition Audience LightSkyBlue {
  === S1 === --&gt; Applauds
}

partition Conductor {
  Bows --&gt; === S2 ===
  --&gt; WavesArmes
  Applauds --&gt; === S2 ===
}

partition Orchestra #CCCCEE {
  WavesArmes --&gt; Introduction
  --&gt; &quot;Play music&quot;
}

@enduml</pre>
<img src='/uml/help/activity/10-Title+the+diagram.uml'>
</div>

<div>
<h2>Skinparam</h2>
<pre>@startuml
title Simple example\nof title 

(*) --&gt; &quot;First activity&quot;
--&gt; (*)
@enduml</pre>
<img src='/uml/help/activity/11-Skinparam.uml'>
</div>

<div>
<h2>Octagon</h2>
<pre>@startuml

skinparam backgroundColor #AAFFFF
skinparam activity {
  StartColor red
  BarColor SaddleBrown 
  EndColor Silver
  BackgroundColor Peru
  BackgroundColor&lt;&lt; Begin &gt;&gt; Olive
  BorderColor Peru
  FontName Impact
}

(*) --&gt; &quot;Climbs on Platform&quot; &lt;&lt; Begin &gt;&gt;
--&gt; === S1 ===
--&gt; Bows
--&gt; === S2 ===
--&gt; WavesArmes
--&gt; (*)

@enduml</pre>
<img src='/uml/help/activity/12-Octagon.uml'>
</div>

<div>
<h2>Complete example</h2>
<pre>@startuml
'Default is skinparam activityShape roundBox
skinparam activityShape octagon

(*) --&gt; &quot;First Activity&quot;
&quot;First Activity&quot; --&gt; (*)

@enduml</pre>
<img src='/uml/help/activity/13-Complete+example.uml'>
</div>



</a:layout>
