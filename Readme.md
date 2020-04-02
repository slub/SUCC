## Saxony / Slub UniCode Converter (SUCC)

The cataloguing in saxony takes place in a central repository (SWB) via WinIBW.
After cataloguing a title the download of this item starts to transfer it to the local system called LIBERO.
The download format from the SWB is MAB tape format, the format LIBERO accepts is MAB disk.
So a converter is needed, not only to translate MAB tape to MAB disk but to to some other manipulation of the data.

In 2009 LIB-IT programmed by the request of and paid for by the saxony libraries a tool called "Unicode Converter" (UCC).

Due to the split up between Insight Informatics (Australia) and LIBT-IT (Germany) the status of the support of the UCC is unsure.
Also is the licensing politic of LIB-IT (regarding the UCC) with the need to provide a stable internet connection and a stable ip address for the client
in times of the growing need for dynamic working locations a great handicap.

Therefore the development of an open source product with similar functions (especially for the SWB communication) has started.

#### calling the program

- java -jar succ.jar [parameter]

    - parameter
        - -function = [ mabt2t | mabt2d | mabd2t | mabd2d ] ... _file formats (mab`<in>`2`<out>`)_
        - -work = [ seq | bulk ] ... _working with data (sequential / bulk)_
        - -convFile = <converter.ini> ... _conversation rules
        - -inFile = <infile> ... _input file_
        - -outFile = <outfile> ... _output file_
        - -propFile = <config.properties> ... _configuration file_
        - -listFile = <listfile> ... _list of files to apply the rules to_
  
   
#### main workflow

- reading command line parameters
- loading config
- overwriting config with command line parameters
- loading converter file into structure
- initializing variables from config
- loading file
- transforming records
- writing file
- display statistics (counts and timings) 
         
