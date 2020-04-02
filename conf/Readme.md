## converter.ini

Rules for the transformation of the records

main structure

    `<typ>_<condition>_<command>_<parameter1>_<parameter2>_`

   - typ = [ l | n | m | p | s | t | c | d | e | k | * |  ]
   
     ... rule will apply to all records of give typ (title, subject, ...) or all (*, )
     
   - condition = `<object>.<operator>.<value>`
        - object ... [ `<MABfield> | V<variable` ]
        - operator
            - ==
            - !=
            - in
            - !in
            - contains
            - !contains
            - startswith
            - !startswith
        - value ... [ `V<variable> | W<value>` ] 
     
        
   - command
        - set ... set `<P1>` to value of `<P2>`
        - copy ... copy value of `<P1>` into value of `<P2>`
        - move ... move value of `<P1>` to  value of `<P2>`
        - delete ... delete `<P1>`
        - prefix ... value of `<P2>` as prefix to value of `<P1>`
        - suffix ... value of `<P2>` as suffix to value of `<P1>`
        - add ... 
        - assign(`<LIST>`) ... set `<P1>` to value of list `<LIST>(<P2>)`
        - piece(`<NR>`, `<DELIM>`) ... split `<P1>` with delimiter `<DELIM>` into `<P2>`
        - format ... format `<P1>` with format string `<P2>`
        - export ... write record (additional) in file `<P1>`


   - P1 / P2 = [ `<MABfield> | V<Variable> | W<value> | C<sysvar>`]
   
   - MABfield = `<MABtag><MABindicator>[<Subfield> |  ]`
   
   - Variable = free naming, also in config.properties with start value
 
<hr>

## config.properties

Configuration for the program (overwritable by command line)