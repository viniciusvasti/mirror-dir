# mirror-dir
A Java console application that mirrors a local directory to an FTP server.
Basically, once the application is connected to an FTP server, it starts to copy files and subdirectories
from your local directory to the FTP server recursively.
If the file already exists, mirror-dir verify if it was locally modified, comparing the "last modified" property of the files.
It uses Threads to run this in a loop.

## Getting Started

### Prerequisites for development and testing purposes
* JDK 8 or higher installed
* Your favorite JAVA IDE (or only the notepad and OS command line if you like old school programming style)

### Prerequisites to run
* JRE 8 or higher installed
* An FTP Server hosting account (I used a free limited account from [DriveHQ](https://www.drivehq.com))

### Running mirror-dir
After opening you Operational System command line terminal:
* you may either run the .jar file located at [MirrorDir.jar](https://github.com/viniciusvasti/mirror-dir/blob/master/dist/MirrorDir.jar):

```C:\User\Downloads>java -jar MirrorDir.jar```
* or run the Main.class once you have all classes compiled:

```C:\User\JavaProjects\MirrorDir\build\classes>java com.vas.mirrordir.main.Main```

The console application is going to ask you for the FTP connection parameters, and then it will start reflecting the directory on the FTP server.
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

Feel free to run/test/create issues and even start pull requests.

ps* at FTPServer.class you can set ```DEBUG``` constant to ```true```, then the FTP commands and responses are going to be printed

## Authors

* **Vinícius A. dos Santos** - [viniciusvasti](https://github.com/viniciusvasti)
