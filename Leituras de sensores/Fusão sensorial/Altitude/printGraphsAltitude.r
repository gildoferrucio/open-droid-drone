library(zoo)
library(lattice)

options(digits.secs = 3)
###Lê os dados a partir do CSV passado
altitudeReadings <- read.zoo("/mnt/dados/Meus Documentos/Estudo/Mestrado/Repositório/trunk/Material pesquisa/Leituras de sensores/Fusão sensorial/Altitude/quadcopterControllerLogSensory_altitude_barometerStatic.csv", index.column = 1, sep = ",", header = TRUE, FUN = as.POSIXct, format='%H:%M:%OS')
#as.xts(altitudeReadings)
#leituras <- as.xts(altitudeReadings, names=c("pressão atmosférica", "temperatura ambiente", "altitude absoluta"))

###Renomea o nome das colunas
colnames(altitudeReadings) [1:4] <- c(expression("Barômetro [m]", "Sonar inferior [cm]", "Altitude absoluta [m]", "Altitude relativa [m]"))

###Imprime o gráfico de linhas
#lwd---> line width
#col---> color (red, orange, blue, blue, green and combinations with dark[nome_cor] or light[nome_cor]), to list all combinations use function colors()
#type--->"g"(grid), "l"(line), "p"(point), "r"(linear regression), "smooth"
#las--->labels are parallel (=0) or perpendicular(=2) to axis
#pch--->point character
#cex--->character extension
#lty--->line type
xyplot(altitudeReadings, superpose=FALSE, type=c("g", "l"), pch=c(1:14), cex=1, lty=c(1), scales=list(x=list(rot=0),y=list(rot=0)), main="Sensor de altitude - Barômetro estático", xlab="Tempo (segs)", ylab="Medidas", las=2, lwd=1)

boxplot(coredata(altitudeReadings$"Barômetro [m]"), main="Altitude - Barômetro estático", ylab="Barômetro (m)", las=2)

##############################################################################################
options(digits.secs = 3)
###Lê os dados a partir do CSV passado
altitudeReadings <- read.zoo("/mnt/dados/Meus Documentos/Estudo/Mestrado/Repositório/trunk/Material pesquisa/Leituras de sensores/Fusão sensorial/Altitude/quadcopterControllerLogSensory_altitude_barometerStatic.csv", index.column = 1, sep = ",", header = TRUE, FUN = as.POSIXct, format='%H:%M:%OS')
#as.xts(altitudeReadings)
#leituras <- as.xts(altitudeReadings, names=c("pressão atmosférica", "temperatura ambiente", "altitude absoluta"))

###Renomea o nome das colunas
colnames(altitudeReadings) [1:4] <- c(expression("Barometer [m]", "Bottom sonar [cm]", "Absolut altitude [m]", "Relative altitude [m]"))

###Imprime o gráfico de linhas
#lwd---> line width
#col---> color (red, orange, blue, blue, green and combinations with dark[nome_cor] or light[nome_cor]), to list all combinations use function colors()
#type--->"g"(grid), "l"(line), "p"(point), "r"(linear regression), "smooth"
#las--->labels are parallel (=0) or perpendicular(=2) to axis
#pch--->point character
#cex--->character extension
#lty--->line type
xyplot(altitudeReadings, superpose=FALSE, type=c("g", "l"), scales=list(x=list(rot=0),y=list(rot=0)), pch=c(1:14), cex=1, lty=c(1), main="Altitude sensor", xlab="Time (secs)", ylab="Measurements", las=2, lwd=1)

boxplot(coredata(altitudeReadings$"Barometer [m]"), main="Altitude sensor", ylab="Barometer (m)", las=0)

###Executar até antes desse comentário, depois de salvar todas as imagens executar o reset de par()

###Reseta os valores de par() apagando todas as fotos do RStudio
dev.off()
