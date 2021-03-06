# mirror-dir
Uma aplicação java em linha de comando para refletir um diretório local em um servidor FTP.
Basicamente, uma vez que a aplicação está conectada ao servidor FTP, ela começa a copiar arquivos e subdiretórios
do diretório local desejado para o servidor FTP recursivamente.
Se o arquivo já existe, o mirror-dir verificar se este fora alterado localmente,
comparando a propriedade "last modified" dos arquivos. Uma Thread é utilizada para repetir o processo constantemente.

## Sem bibliotecas FTP de terceiros
Eu sei que eu poderia usar a classe FTPClient da Apache Commons ou outras bibliotecas Java prontas, mas, com o objetivo
de demonstrar habilidades, eu escolhi implementar minha própria classe de cliente FTP, usando sockets para conectar
com o servidor FTP e input/output streams.

## Mãos a obra

### Pré-requisitos para propósitos de desenvolvimento e testes
* Ter instalado o JDK 8 ou superior
* Sua IDE Java favorita (ou apenas o bloco de notas e prompt de comando do seu Sistema Operacional se você gosta de programar a moda antiga)

### Pré-requisitos para executar a aplicação
* Ter instalado o JRE 8 ou superior
* Ter uma conta em Servidor de hospedagem de arquivos FTP (Eu usei uma conta grátis e limitada do [DriveHQ](https://www.drivehq.com))

### Executando o mirror-dir
Após abrir o console de linha de comando do seu Sistema Operacional:
* você pode executar o arquivo .jar localizado em [MirrorDir.jar](https://github.com/viniciusvasti/mirror-dir/blob/master/dist/MirrorDir.jar):

```C:\User\Downloads>java -jar MirrorDir.jar```
* ou executar a classe Main.class, desde que você possua todas as classes do projeto compiladas:

```C:\User\JavaProjects\MirrorDir\build\classes>java com.vas.mirrordir.main.Main```

Então, a aplicação pedirá os parâmetros de conexão FTP, para em seguida começar a refletir o diretório local no servidor FTP:
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

### Erros (possíveis) conhecidos
* Na classe [RemoteMirror](https://github.com/viniciusvasti/mirror-dir/blob/master/src/com/vas/mirrordir/controllers/RemoteMirror.java), o método lastTimeModified retorna, em milisegundos, a data da última motificação do arquivo. Uma vez que o horário retornado pelo servidor está em UTC e o meu fuso horário é UTC-3, eu subtraí 3 da hora. É preciso comparar o fuso do servidor com o do usuário para fazer o ajuste adequado para  a hora:
```
LocalDateTime date = LocalDateTime.of(
                        Integer.parseInt(dateString.substring(0, 4)), // year
                        Integer.parseInt(dateString.substring(4, 6)), // month
                        Integer.parseInt(dateString.substring(6, 8)), //day
                        Integer.parseInt(dateString.substring(8, 10)) - 3, // hour
                        Integer.parseInt(dateString.substring(10, 12)), // minute
                        Integer.parseInt(dateString.substring(12)) // second
);
```
* Ao enviar arquivos para o servidor, por algum motivo que desconheço, estão sendo acrescentados 3kb em cada arquivo

## Contribuindo

Fique a vontade para executar, testar, criar issues e enviar seus pull requests.

ps* na classe FTPClient você pode alterar a constante ```DEBUG``` para ```true```, assim os comandos e respostas FTP serão exibidos no console.

## Autores

* **Vinícius A. dos Santos** - [viniciusvasti](https://github.com/viniciusvasti)
