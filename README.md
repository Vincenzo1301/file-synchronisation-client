# File Synchronization Client

The task is to write a base for a remote file synchronisation system using Java serialization. In this task you only
synchronise the information about the files (names, modification dates, etc.) between two ends, not the files
themselves (contents). Your client program collects information about all files in a given directory through the
java.io.File class, stores them in a suitable data structure, for example a set of files – Set<java.io.File>. The client
then establishes a connection with a file synchronisation server and sends the serialized list of files to the server
along with the client identifier. After de-serializing the list, the server then checks its records (again, a simple
file or RAM stored data is fine) of files for the given client and identifies the files that are new or different (the
comparison of two file sets can be done by suitable API classes and calls from the Java collections library). The list
of the files that the server identified as new (had no knowledge of before) is sent back to the client (again using
serialization). Normally, the client would initialize the backup procedure for the new files, for this task it is
sufficient that you simply print the list of the new files that need to be backed up. And just to repeat and clarify the
often reoccurring question: it is not part of this task to send any file contents over, only the information about the
files so that the client knows which files to back up.

Hint: As a first step you can make your server local and just send the serialized list through a simple method call
passing on the serialization stream, rather than building up a proper TCP/IP connection. You can then expand this method
to have the data stream over a network connection.

Note: getting a list of files using the File class on OS-es supporting soft links (Mac or Linux) can be tricky. Soft
linked directories can (and do when we talk about the complete file system!) generally make file structures circular in
which case the a naive file information retrieval procedure would not terminate. On these systems it is wise and
advisable to skip files that are soft links. For that you will need to use some classes and methods from the New IO Java
package – java.nio. Consult Google on how to do this. Or, to quickly avoid the problem - simply run your program on a
test directory that does not have any soft links to start with.

Optional

Do the same task, but instead of the Java serialization interface, use the JSON format to encode and decode the
transmitted data. For this you can use the gson library from Google discussed during one of the lectures. Previous
versions of this task gave the option to do this using the XML format through JAXB library. You can still choose this
route, however, the JAXB library is not part of the standard Java installation anymore, so you are on your own on how to
set up JAXB or which other XML library you could use for this.