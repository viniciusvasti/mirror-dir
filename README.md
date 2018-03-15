# mirror-dir
A Java console application that mirrors a local directory to a FTP server
Basically, once the application is connected to a FTP server, it starts to copy files and subdirectories
from your local directory to FTP server recursively.
If the file already exists, mirror-dir verify if it was modified locally comparing the "last modified" property of the files.
It uses Threads to run this in a loop

## Getting Started

### Prerequisites for development and testing purposes
* Have installed JDK 8 or higher
* Your fevorite JAVA IDE (or only the notepad and OS command line if like old school programming style)

### Prerequisites to run
* JRE 8 or higher
* An FTP Server hosting account (I used a free limited account from [DriveHQ](https://www.drivehq.com))

### Running mirror-dir
After open you Operational System command line terminal:
* you can run either the .jar file located at [MirrorDir.jar](https://github.com/viniciusvasti/mirror-dir/blob/master/dist/MirrorDir.jar):

```C:\User\Downloads>java -jar MirrorDir.jar```
* or run the Main.class once you have all classes compiled:

```C:\User\JavaProjects\MirrorDir\build\classes>java com.vas.mirrordir.main.Main```

Then, the console application gonna ask you for FTP connection parameters then start reflecting the directory to FTP server
```
### MirrorDir config ###
>Local directory (e.g. C:\Vinicius\Documents\DirectoryToMirror):
C:\Users\Vinicius\Desktop\mirrorOrigin
>FTP domain (e.g. ftp.drivehq.com):
ftp.drivehq.com
>FTP user:
user123
>FTP password:
mypass0101
### MirrorDir config ###
Start reflecting...
Finish reflecting...
Start reflecting...
Finish reflecting...
Start reflecting...
Finish reflecting...
```

## Contributing

Be free to run/test/create issues/pull requests

ps* at FTPServer.class you can set ```DEBUG``` constant to ```true```, then the FTP commands and responses gonna be printed

## Authors

* **Vinícius A. dos Santos** - [viniciusvasti](https://github.com/viniciusvasti)
