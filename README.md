# Setup

## Creating a runnable JAR from the project (from eclipse) if there isnt one already
------------------------------------------------------------------------------------
#### Setting up run config
1. Right-click the Java project on the project explorer
2. Select "Run As" -> "Run Configurations"
3. Select the "arguments" tab, and paste "${file_prompt}" into the "Program Arguments" field
4. Name the config "MiniTS" then click apply

#### Creating a runnable JAR
**there already exists a runnable JAR so there is no need to create a new one**
1. Right-click the Java project on the project explorer
2. Click "export"
3. Select Java -> Runnable JAR File
4. Set the launch configuration to be the same as the run config
5. Select export destination to be "MiniTS\miniTs.jar"

## Running the Runnable JAR
---------------------------
Before running, make sure you are in the **root** directory (MiniTS).
	ie: /mnt/c/Users/russe/Github/MiniTS
	
When running the input command, you can select to provide either:
1. A directory full of .txt files 
2. 1 individual file

Using the command: java -jar miniTs.jar {input}

#### Running the 10 example files
1. Run the following command:
	java -jar miniTs.jar src/tests/finalSubmission/section5
	
2. Refresh the test folder (src/tests) and they should be in the format
	src/tests/<filename>-report.html

3. You can either view the results of a singular file (<filename>-report.html)
	or see all 10 results at the same time (preferred) in the file
	combined_report.html

#### Running an individual file
1. Run the following command:
	java -jar minits.jar <filepath>/<filename>.txt
	
	ex: java -jar miniTs.jar src/tests/finalSubmission/section5/inventory.txt
	
2. Test results should be in src/tests/<filename>-report.html
