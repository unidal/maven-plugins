<%@ page contentType="text/html; charset=utf-8"%>
<%@ taglib prefix="a" uri="/WEB-INF/app.tld"%>

<a:layout>

<br>

<div>
<h2>Simple Activity</h2>
<pre>@startuml

:Hello world;

:This is on defined on
several **lines**;

@enduml</pre>
<img src='/uml/help/activity2/01-Simple+Activity.uml'>
</div>

<div>
<h2>Start Stop</h2>
<pre>@startuml

start

:Hello world;

:This is on defined on
several **lines**;

stop

@enduml</pre>
<img src='/uml/help/activity2/02-Start+Stop.uml'>
</div>

<div>
<h2>Conditional</h2>
<pre>@startuml

start

if (graphviz installed?) then (yes)
  :process all\ndiagrams;
else (no)
  :process only
  __sequence__ and __activity__ diagrams;
endif

stop

@enduml</pre>
<img src='/uml/help/activity2/03-Conditional.uml'>
</div>

<div>
<h2>Repeat loop</h2>
<pre>@startuml

start

repeat
  :read data;
  :generate diagrams;
repeat while (more data?)

stop

@enduml</pre>
<img src='/uml/help/activity2/04-Repeat+loop.uml'>
</div>

<div>
<h2>While loop</h2>
<pre>@startuml

start

while (data available?)
  :read data;
  :generate diagrams;
endwhile

stop

@enduml</pre>
<img src='/uml/help/activity2/05-While+loop.uml'>
</div>

<div>
<h2>Parallel processing</h2>
<pre>@startuml
while (check filesize ?) is (not empty)
  :read file;
endwhile (empty)
:close file;
@enduml</pre>
<img src='/uml/help/activity2/06-Parallel+processing.uml'>
</div>

<div>
<h2>Notes</h2>
<pre>@startuml

start

if (multiprocessor?) then (yes)
  fork
    :Treatment 1;
  fork again
    :Treatment 2;
  end fork
else (monoproc)
  :Treatment 1;
  :Treatment 2;
endif


@enduml</pre>
<img src='/uml/help/activity2/07-Notes.uml'>
</div>

<div>
<h2>Color</h2>
<pre>@startuml

start
:foo1;
note left: This is a note
:foo2;
note right
  This note is on several
  //lines// and can
  contain &lt;b&gt;HTML&lt;/b&gt;
  ====
  * Calling the method &quot;&quot;foo()&quot;&quot; is prohibited
end note
stop

@enduml</pre>
<img src='/uml/help/activity2/08-Color.uml'>
</div>

<div>
<h2>Complete example</h2>
<pre>@startuml

start
:starting progress;
:#red:reading configuration files
These files must do be edited at this point!;
:#AAAAAA:ending of the process;

@enduml</pre>
<img src='/uml/help/activity2/09-Complete+example.uml'>
</div>



</a:layout>
