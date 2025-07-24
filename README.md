# Running the Application
* Navigate to the src folder
* java -jar EECS4302Compiler.jar {input}

Input Parameter
* When runniing the input command, we can provide either a directory full of .txt files, or the path of 1 individual file
* e.g. java -jar EECS4302Compiler.jar tests/input/

# Expected Output
* ## Single Report
  *   This single report named {inputFileName-report.html} will show the input file name, input file contents, final variable states, and errors.
  *   This report will be generated into the tests/output folder.
  *   Final variable states are highlighted in blue
  *   Errors are highlighted in red
  
* ## Combined Report
  *   This combined report named (combined-report.html} will show all the single reports from a directory of files, on one singular page
  *   This report will be generated into the tests/output folder
  *   Final variable states are highlighted in blue
  *   Errors are highlighted in red
  *   At the top of each single report within the combined report, the name of the input file is a hyperlink which will direct you to the single report for that input file
 
* # Samples
  * ## Single Reports
<img width="2554" height="1265" alt="image" src="https://github.com/user-attachments/assets/10d51d84-aae7-46be-b1f1-bcaa374bf00c" />
<img width="2551" height="1262" alt="image" src="https://github.com/user-attachments/assets/fbc473f7-6021-492c-b6fe-5f681b9f35ca" />

  * ## Combined Report
<img width="2551" height="1263" alt="image" src="https://github.com/user-attachments/assets/1613b3a9-c853-419b-b5e0-aeb34c10271e" />
