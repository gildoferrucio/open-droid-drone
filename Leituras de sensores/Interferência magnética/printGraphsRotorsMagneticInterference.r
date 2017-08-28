library(zoo)
library(lattice)

options(digits.secs=3)
###Lê os dados a partir do CSV passado
rotorsMagneticInterference <- read.zoo("/mnt/dados/Meus Documentos/Estudo/Mestrado/Repositório/trunk/Material pesquisa/Leituras de sensores/Interferência magnética/rotorsMagneticInterference.csv", index.column=1, sep=",", header=TRUE, FUN = as.POSIXct, format='%H:%M:%OS')
#as.xts(rotorsMagneticInterference)
#leituras <- as.xts(rotorsMagneticInterference, names=c("pressão atmosférica", "temperatura ambiente", "altitude absoluta"))

###Renomea o nome das colunas
colnames(rotorsMagneticInterference) [1:5] <- c("Direção [°]", "Rotor 1 [RPM]", "Rotor 2 [RPM]", "Rotor 3 [RPM]", "Rotor 4 [RPM]")

###Imprime o gráfico de linhas
#lwd---> line width
#col---> color (red, orange, blue, blue, green and combinations with dark[nome_cor] or light[nome_cor]), to list all combinations use function colors()
#type--->"g"(grid), "l"(line), "p"(point), "r"(linear regression), "smooth"
#las--->labels are parallel (=0) or perpendicular(=2) to axis
#pch--->point character
#cex--->character extension
#lty--->line type
xyplot(rotorsMagneticInterference, superpose=FALSE, type=c("g", "l"), scales=list(x=list(rot=0),y=list(rot=0)), pch=c(1:14), cex=1, main="Interferência magnética dos rotores", xlab="Tempo (hh:mm:ss)", ylab="Medidas", las=2, lwd=1)

par(mfrow=c(2,2), oma=c(0,0,2,0))
boxplot(coredata(rotorsMagneticInterference$"Direção [°]"), main="Interferência magnética dos rotores", ylab="Direção (°)", las=2)
##boxplot(coredata(rotorsMagneticInterference$"Rotor 1 [RPM]"), main="Rotor 1", ylab="Vel. de rotação (RPM)", las=2)
##boxplot(coredata(rotorsMagneticInterference$"Rotor 2 [RPM]"), main="Rotor 2", ylab="Vel. de rotação (RPM)", las=2)
##boxplot(coredata(rotorsMagneticInterference$"Rotor 3 [RPM]"), main="Rotor 3", ylab="Vel. de rotação (RPM)", las=2)
##boxplot(coredata(rotorsMagneticInterference$"Rotor 4 [RPM]"), main="Rotor 4", ylab="Vel. de rotação (RPM)", las=2)
###Imprime um título para o conjunto de gráficos
#cex--->character extension, it means characters size
mtext("Calibração dos rotores", outer=TRUE, cex=1.35)

###################################################################
options(digits.secs=3)
###Lê os dados a partir do CSV passado
rotorsMagneticInterference <- read.zoo("/mnt/dados/Meus Documentos/Estudo/Mestrado/Repositório/trunk/Material pesquisa/Leituras de sensores/Interferência magnética/rotorsMagneticInterference.csv", index.column=1, sep=",", header=TRUE, FUN = as.POSIXct, format='%H:%M:%OS')
#as.xts(rotorsMagneticInterference)
#leituras <- as.xts(rotorsMagneticInterference, names=c("pressão atmosférica", "temperatura ambiente", "altitude absoluta"))

###Renomea o nome das colunas
colnames(rotorsMagneticInterference) [1:5] <- c("Compass Heading [°]", "Rotor #1 [RPM]", "Rotor #2 [RPM]", "Rotor #3 [RPM]", "Rotor #4 [RPM]")

###Imprime o gráfico de linhas
#lwd---> line width
#col---> color (red, orange, blue, blue, green and combinations with dark[nome_cor] or light[nome_cor]), to list all combinations use function colors()
#type--->"g"(grid), "l"(line), "p"(point), "r"(linear regression), "smooth"
#las--->labels are parallel (=0) or perpendicular(=2) to axis
#pch--->point character
#cex--->character extension
#lty--->line type
xyplot(rotorsMagneticInterference, superpose=FALSE, type=c("g", "l"), scales=list(x=list(rot=0),y=list(rot=0)), pch=c(1:14), cex=1, main="Rotor's magnetic interference", xlab="Time (hh:mm:ss)", ylab="Meeasurements", las=2, lwd=1)

par(mfrow=c(2,2), oma=c(0,0,2,0))
boxplot(coredata(rotorsMagneticInterference$"Compass Heading [°]"), main="Rotor's magnetic interference", ylab="Compass Heading (°)", las=1)
boxplot(coredata(rotorsMagneticInterference$"Rotor #1 [RPM]"), main="Rotor #1", ylab="Revolutions (RPM)", las=0)
boxplot(coredata(rotorsMagneticInterference$"Rotor #2 [RPM]"), main="Rotor #2", ylab="Revolutions (RPM)", las=0)
boxplot(coredata(rotorsMagneticInterference$"Rotor #3 [RPM]"), main="Rotor #3", ylab="Revolutions (RPM)", las=0)
boxplot(coredata(rotorsMagneticInterference$"Rotor #4 [RPM]"), main="Rotor #4", ylab="Revolutions (RPM)", las=0)
###Imprime um título para o conjunto de gráficos
#cex--->character extension, it means characters size
mtext("Calibração dos rotores", outer=TRUE, cex=1.35)

###Executar até antes desse comentário, depois de salvar todas as imagens executar o reset de par()

###Reseta os valores de par() apagando todas as fotos do RStudio
dev.off()
