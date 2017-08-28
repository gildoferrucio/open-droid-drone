library(zoo)
library(lattice)

options(digits.secs = 3)
###Lê os dados a partir do CSV passado
rotorGateFunctionTestBrakeOff <- read.zoo("/mnt/dados/Meus Documentos/Estudo/Mestrado/Repositório/trunk/Material pesquisa/Leituras de sensores/Controle/Rotação dos motores/gateFunctionTest-brakeOff.csv", index.column = 1, sep = ",", header = TRUE, FUN = as.POSIXct, format='%H:%M:%OS')
#as.xts(rotorGateFunctionTestBrakeOff)
#leituras <- as.xts(rotorGateFunctionTestBrakeOff, names=c("pressão atmosférica", "temperatura ambiente", "altitude absoluta"))

###Renomea o nome das colunas
colnames(rotorGateFunctionTestBrakeOff) [1:2] <- c(expression("Tempo de ciclo ativo [µsec]", "Revoluções [RPM]"))

###Imprime o gráfico de linhas
#lwd---> line width
#col---> color (red, orange, blue, blue, green and combinations with dark[nome_cor] or light[nome_cor]), to list all combinations use function colors()
#type--->"g"(grid), "l"(line), "p"(point), "r"(linear regression), "smooth"
#las--->labels are parallel (=0) or perpendicular(=2) to axis
#pch--->point character
#cex--->character extension
#lty--->line type
xyplot(rotorGateFunctionTestBrakeOff, superpose=FALSE, type=c("g", "l"), scales=list(x=list(rot=0),y=list(rot=0)), pch=c(1:14), cex=1, lty=c(1), main="Rotores - Resposta à função porta - Freio desligado", xlab="Tempo (segs)", ylab="Medidas", las=2, lwd=1)

#boxplot(coredata(rotorGateFunctionTestBrakeOff$"Barômetro [m]"), main="Altitude - Barômetro estático", ylab="Barômetro (m)", las=0)

################################################################
options(digits.secs = 3)
###Lê os dados a partir do CSV passado
rotorGateFunctionTestBrakeOn <- read.zoo("/mnt/dados/Meus Documentos/Estudo/Mestrado/Repositório/trunk/Material pesquisa/Leituras de sensores/Controle/Rotação dos motores/gateFunctionTest-brakeOn.csv", index.column = 1, sep = ",", header = TRUE, FUN = as.POSIXct, format='%H:%M:%OS')
#as.xts(rotorGateFunctionTestBrakeOn)
#leituras <- as.xts(rotorGateFunctionTestBrakeOn, names=c("pressão atmosférica", "temperatura ambiente", "altitude absoluta"))

###Renomea o nome das colunas
colnames(rotorGateFunctionTestBrakeOn) [1:2] <- c(expression("Tempo de ciclo ativo [µsec]", "Revoluções [RPM]"))

###Imprime o gráfico de linhas
#lwd---> line width
#col---> color (red, orange, blue, blue, green and combinations with dark[nome_cor] or light[nome_cor]), to list all combinations use function colors()
#type--->"g"(grid), "l"(line), "p"(point), "r"(linear regression), "smooth"
#las--->labels are parallel (=0) or perpendicular(=2) to axis
#pch--->point character
#cex--->character extension
#lty--->line type
xyplot(rotorGateFunctionTestBrakeOn, superpose=FALSE, type=c("g", "l"), scales=list(x=list(rot=0),y=list(rot=0)), pch=c(1:14), cex=1, lty=c(1), main="Rotores - Resposta à função porta - Freio ligado", xlab="Tempo (segs)", ylab="Medidas", las=2, lwd=1)

#boxplot(coredata(rotorGateFunctionTestBrakeOn$"Barômetro [m]"), main="Altitude - Barômetro estático", ylab="Barômetro (m)", las=0)
###Executar até antes desse comentário, depois de salvar todas as imagens executar o reset de par()

###Reseta os valores de par() apagando todas as fotos do RStudio
dev.off()
