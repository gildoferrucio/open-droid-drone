library(zoo)
library(lattice)

options(digits.secs = 3)
###Lê os dados a partir do CSV passado
triangleFunctionTestLinearR1 <- read.zoo("/mnt/dados/Meus Documentos/Estudo/Mestrado/Repositório/trunk/Material pesquisa/Leituras de sensores/Controle/Rotação dos motores/Identificação sistema/triangleFunctionTestLinearR1.csv", index.column = 1, sep = ",", header = TRUE, FUN = as.POSIXct, format='%H:%M:%OS')
#as.xts(triangleFunctionTestLinearR1)
#leituras <- as.xts(triangleFunctionTestLinearR1, names=c("pressão atmosférica", "temperatura ambiente", "altitude absoluta"))

###Renomea o nome das colunas
colnames(triangleFunctionTestLinearR1) [1:2] <- c(expression("Tempo de ciclo ativo [µsec]", "Revoluções [RPM]"))

###Imprime o gráfico de linhas
#lwd---> line width
#col---> color (red, orange, blue, blue, green and combinations with dark[nome_cor] or light[nome_cor]), to list all combinations use function colors()
#type--->"g"(grid), "l"(line), "p"(point), "r"(linear regression), "smooth"
#las--->labels are parallel (=0) or perpendicular(=2) to axis
#pch--->point character
#cex--->character extension
#lty--->line type
xyplot(triangleFunctionTestLinearR1, superpose=FALSE, type=c("g", "l"), scales=list(x=list(rot=0),y=list(rot=0)), pch=c(1:14), cex=1, lty=c(1), main="Resposta à onda triangular - Rotor 1", xlab="Tempo (segs)", ylab="Medidas", las=2, lwd=1)

#boxplot(coredata(triangleFunctionTestLinearR1$"Barômetro [m]"), main="Altitude - Barômetro estático", ylab="Barômetro (m)", las=0)

################################################################
options(digits.secs = 3)
###Lê os dados a partir do CSV passado
triangleFunctionTestLinearR2 <- read.zoo("/mnt/dados/Meus Documentos/Estudo/Mestrado/Repositório/trunk/Material pesquisa/Leituras de sensores/Controle/Rotação dos motores/Identificação sistema/triangleFunctionTestLinearR2.csv", index.column = 1, sep = ",", header = TRUE, FUN = as.POSIXct, format='%H:%M:%OS')
#as.xts(triangleFunctionTestLinearR2)
#leituras <- as.xts(triangleFunctionTestLinearR2, names=c("pressão atmosférica", "temperatura ambiente", "altitude absoluta"))

###Renomea o nome das colunas
colnames(triangleFunctionTestLinearR2) [1:2] <- c(expression("Tempo de ciclo ativo [µsec]", "Revoluções [RPM]"))

###Imprime o gráfico de linhas
#lwd---> line width
#col---> color (red, orange, blue, blue, green and combinations with dark[nome_cor] or light[nome_cor]), to list all combinations use function colors()
#type--->"g"(grid), "l"(line), "p"(point), "r"(linear regression), "smooth"
#las--->labels are parallel (=0) or perpendicular(=2) to axis
#pch--->point character
#cex--->character extension
#lty--->line type
xyplot(triangleFunctionTestLinearR2, superpose=FALSE, type=c("g", "l"), scales=list(x=list(rot=0),y=list(rot=0)), pch=c(1:14), cex=1, lty=c(1), main="Resposta à onda triangular - Rotor 2", xlab="Tempo (segs)", ylab="Medidas", las=2, lwd=1)

#boxplot(coredata(triangleFunctionTestLinearR2$"Barômetro [m]"), main="Altitude - Barômetro estático", ylab="Barômetro (m)", las=0)

################################################################
options(digits.secs = 3)
###Lê os dados a partir do CSV passado
triangleFunctionTestLinearR3 <- read.zoo("/mnt/dados/Meus Documentos/Estudo/Mestrado/Repositório/trunk/Material pesquisa/Leituras de sensores/Controle/Rotação dos motores/Identificação sistema/triangleFunctionTestLinearR3.csv", index.column = 1, sep = ",", header = TRUE, FUN = as.POSIXct, format='%H:%M:%OS')
#as.xts(triangleFunctionTestLinearR3)
#leituras <- as.xts(triangleFunctionTestLinearR3, names=c("pressão atmosférica", "temperatura ambiente", "altitude absoluta"))

###Renomea o nome das colunas
colnames(triangleFunctionTestLinearR3) [1:2] <- c(expression("Tempo de ciclo ativo [µsec]", "Revoluções [RPM]"))

###Imprime o gráfico de linhas
#lwd---> line width
#col---> color (red, orange, blue, blue, green and combinations with dark[nome_cor] or light[nome_cor]), to list all combinations use function colors()
#type--->"g"(grid), "l"(line), "p"(point), "r"(linear regression), "smooth"
#las--->labels are parallel (=0) or perpendicular(=2) to axis
#pch--->point character
#cex--->character extension
#lty--->line type
xyplot(triangleFunctionTestLinearR3, superpose=FALSE, type=c("g", "l"), scales=list(x=list(rot=0),y=list(rot=0)), pch=c(1:14), cex=1, lty=c(1), main="Resposta à onda triangular - Rotor 3", xlab="Tempo (segs)", ylab="Medidas", las=2, lwd=1)

#boxplot(coredata(triangleFunctionTestLinearR3$"Barômetro [m]"), main="Altitude - Barômetro estático", ylab="Barômetro (m)", las=0)

################################################################
options(digits.secs = 3)
###Lê os dados a partir do CSV passado
triangleFunctionTestLinearR4 <- read.zoo("/mnt/dados/Meus Documentos/Estudo/Mestrado/Repositório/trunk/Material pesquisa/Leituras de sensores/Controle/Rotação dos motores/Identificação sistema/triangleFunctionTestLinearR4.csv", index.column = 1, sep = ",", header = TRUE, FUN = as.POSIXct, format='%H:%M:%OS')
#as.xts(triangleFunctionTestLinearR4)
#leituras <- as.xts(triangleFunctionTestLinearR4, names=c("pressão atmosférica", "temperatura ambiente", "altitude absoluta"))

###Renomea o nome das colunas
colnames(triangleFunctionTestLinearR4) [1:2] <- c(expression("Tempo de ciclo ativo [µsec]", "Revoluções [RPM]"))

###Imprime o gráfico de linhas
#lwd---> line width
#col---> color (red, orange, blue, blue, green and combinations with dark[nome_cor] or light[nome_cor]), to list all combinations use function colors()
#type--->"g"(grid), "l"(line), "p"(point), "r"(linear regression), "smooth"
#las--->labels are parallel (=0) or perpendicular(=2) to axis
#pch--->point character
#cex--->character extension
#lty--->line type
xyplot(triangleFunctionTestLinearR4, superpose=FALSE, type=c("g", "l"), scales=list(x=list(rot=0),y=list(rot=0)), pch=c(1:14), cex=1, lty=c(1), main="Resposta à onda triangular - Rotor 4", xlab="Tempo (segs)", ylab="Medidas", las=2, lwd=1)

#boxplot(coredata(triangleFunctionTestLinearR4$"Barômetro [m]"), main="Altitude - Barômetro estático", ylab="Barômetro (m)", las=0)

###Executar até antes desse comentário, depois de salvar todas as imagens executar o reset de par()

###Reseta os valores de par() apagando todas as fotos do RStudio
dev.off()
