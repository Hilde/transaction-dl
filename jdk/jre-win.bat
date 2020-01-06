deltree jre-win
win\jdk-13.0.1\bin\jlink.exe --compress=2 --strip-debug --module-path "win\jdk-13.0.1\jmods;win\javafx-jmods-11.0.2" --add-modules java.base,java.logging,java.naming,javafx.base,javafx.controls,javafx.fxml --output jre-win
