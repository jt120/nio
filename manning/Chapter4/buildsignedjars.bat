@echo off
jar cvf classes.jar PicoDraw*.class DrawCanvas*.class TransferableImage*.class
jar cvf backgrounds.jar backgrounds.txt backgrounds/*.gif
jarsigner -keystore manning -storepass jdk14tut classes.jar manning
jarsigner -keystore manning -storepass jdk14tut backgrounds.jar manning
