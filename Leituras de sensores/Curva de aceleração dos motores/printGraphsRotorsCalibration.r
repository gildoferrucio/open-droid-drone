library(zoo)
library(lattice)

options(digits.secs=3)
###Lê os dados a partir do CSV passado
rotorsCalibration <- read.zoo("/mnt/dados/Meus Documentos/Estudo/Mestrado/Repositório/trunk/Material pesquisa/Leituras de sensores/Curva de aceleração dos motores/quadcopterControllerRotorsCalibration.csv", index.column=0, sep=",", header=TRUE, FUN=as.numeric)
#as.xts(rotorsCalibration)
#leituras <- as.xts(rotorsCalibration, names=c("pressão atmosférica", "temperatura ambiente", "altitude absoluta"))

###Renomea o nome das colunas
colnames(rotorsCalibration) [1:4] <- c("Rotor 1", "Rotor 2", "Rotor 3", "Rotor 4")

###Imprime o gráfico de linhas
#lwd---> line width
#col---> color (red, orange, blue, blue, green and combinations with dark[nome_cor] or light[nome_cor]), to list all combinations use function colors()
#type--->"g"(grid), "l"(line), "p"(point), "r"(linear regression), "smooth"
#las--->labels are parallel (=0) or perpendicular(=2) to axis
#pch--->point character
#cex--->character extension
#lty--->line type
xyplot(rotorsCalibration, superpose=TRUE, auto.key=list(space="top", columns=2, lines=TRUE, points=TRUE), scales=list(x=list(rot=0),y=list(rot=0)), type=c("g", "l", "p"), pch=c(1:14), cex=1, lty=c(1:4), main="Calibração dos rotores", xlab="Acelerador (%)", ylab="Vel. de rotação (RPM)", las=2, lwd=1)

par(mfrow=c(2,2), oma=c(0,0,2,0))
boxplot(coredata(rotorsCalibration$"Rotor 1"), main="Rotor 1", ylab="Vel. de rotação (RPM)", las=0)
boxplot(coredata(rotorsCalibration$"Rotor 2"), main="Rotor 2", ylab="Vel. de rotação (RPM)", las=0)
boxplot(coredata(rotorsCalibration$"Rotor 3"), main="Rotor 3", ylab="Vel. de rotação (RPM)", las=0)
boxplot(coredata(rotorsCalibration$"Rotor 4"), main="Rotor 4", ylab="Vel. de rotação (RPM)", las=0)
###Imprime um título para o conjunto de gráficos
#cex--->character extension, it means characters size
mtext("Calibração dos rotores", outer=TRUE, cex=1.35)

################################################################
options(digits.secs=3)
###Lê os dados a partir do CSV passado
rotorsCalibration <- read.zoo("/mnt/dados/Meus Documentos/Estudo/Mestrado/Repositório/trunk/Material pesquisa/Leituras de sensores/Calibração dos motores/quadcopterControllerRotorsCalibration.csv", index.column=0, sep=",", header=TRUE, FUN=as.numeric)
#as.xts(rotorsCalibration)
#leituras <- as.xts(rotorsCalibration, names=c("pressão atmosférica", "temperatura ambiente", "altitude absoluta"))

###Renomea o nome das colunas
colnames(rotorsCalibration) [1:4] <- c("Rotor #1", "Rotor #2", "Rotor #3", "Rotor #4")

###Imprime o gráfico de linhas
#lwd---> line width
#col---> color (red, orange, blue, blue, green and combinations with dark[nome_cor] or light[nome_cor]), to list all combinations use function colors()
#type--->"g"(grid), "l"(line), "p"(point), "r"(linear regression), "smooth"
#las--->labels are parallel (=0) or perpendicular(=2) to axis
#pch--->point character
#cex--->character extension
#lty--->line type
xyplot(rotorsCalibration, superpose=TRUE, auto.key=list(space="top", columns=2, lines=TRUE, points=TRUE), scales=list(x=list(rot=0),y=list(rot=0)), type=c("g", "l", "p"), pch=c(1:14), cex=1, lty=c(1:4), main="ESCs calibration", xlab="Throttle (%)", ylab="Revolutions (RPM)", las=2, lwd=1)


###Executar até antes desse comentário, depois de salvar todas as imagens executar o reset de par()

###Reseta os valores de par() apagando todas as fotos do RStudio
dev.off()
