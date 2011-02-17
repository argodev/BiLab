@echo "generating lexer/parser & tree parser from ANTLR sources"

@set JAVA_HOME=C:\Program Files\Java\jdk1.5.0
@set JAVA="%JAVA_HOME%\bin\java.exe"
@set ANTLRJAR=..\..\libs\antlr.jar 
@set ANTLRMAIN=antlr.Tool
@rem -traceTreeParser 
@rem -traceParser

%JAVA% -cp %ANTLRJAR% %ANTLRMAIN% Scigol.g 

%JAVA% -cp %ANTLRJAR% %ANTLRMAIN% Scigol.tree.g 
