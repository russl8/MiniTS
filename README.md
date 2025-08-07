# Running the Application
* From the project folder:
* java -jar EECS4302Compiler.jar {input}

Input Parameter
* When runniing the input command, we can provide either a directory full of .txt files, or the path of 1 individual file
* e.g. java -jar EECS4302Compiler.jar src/tests/

# Expected Output
* ## Single Report
  *   Can be found in the src/tests/ folder
  *   This single report named {inputFileName-report.html} will show the input file name, input file contents, final variable states, and errors.
  *   This report will be generated into the tests/output folder.
  *   Final variable states are highlighted in blue
  *   Errors are highlighted in red
  
* ## Combined Report
  *   Can be found in the src/tests/ folder
  *   This combined report named (combined-report.html} will show all the single reports from a directory of files, on one singular page
  *   This report will be generated into the tests/output folder
  *   Final variable states are highlighted in blue
  *   Errors are highlighted in red
  *   At the top of each single report within the combined report, the name of the input file is a hyperlink which will direct you to the single report for that input file
 
* # Samples
  * ## Single Reports
<img width="2527" height="998" alt="image" src="https://github.com/user-attachments/assets/f048e948-b576-493e-899f-be137d131937" />
<img width="2497" height="893" alt="image" src="https://github.com/user-attachments/assets/6d88d987-32e5-46ef-9735-1d29a152ac6f" />

  * ## Combined Report
<img width="2448" height="1239" alt="image" src="https://github.com/user-attachments/assets/1a852619-51f4-408e-8cfb-d55ce8f5a6f1" />
<img width="2506" height="829" alt="image" src="https://github.com/user-attachments/assets/05af6f04-d61d-40c0-9ba7-35c12c5cbe3e" />


