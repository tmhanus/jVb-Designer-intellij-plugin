# jVb-Designer-intellij-plugin
This is **jVb Designer** plugin for IntelliJ IDEA. It is a graphical editor for job structure configuration in Batching API. It allows you a bi-directional propagation of changes between both text and graphical representation of batch job.

Functionality
--------------
Supported Elements:
   * *Step Elements* (chunk and batchlet)
   * *Flow Elements*
   * *Split Elements*
   * *Decision Elements*
   * *End Transition Elements* (End, Fail, Stop)
   * *Start Element* to set first element
  
Screenshots
--------------
**Designer Canvas**

![Alt text](/images/all.png "Designer Canvas")

**Palette & Properties Panel**

![Alt text](/images/panels.png "Palette & Properties Panel")

**Editor example**

![Alt text](/images/exceptions.png "Editor example")

Hints:
--------------
  * to create a new Job definition file: New --> Job Definition File
  * to submit changes and generate final Definition File (.xml): RMB (Right mouse button) --> Generate Job File
  * edit .jsl file in both text and graphical editor (Do not edit .jsd file. It's just an inner configuration file, which stores positions and other additional info about elements. Otherwise be aware of possible consequences.).



