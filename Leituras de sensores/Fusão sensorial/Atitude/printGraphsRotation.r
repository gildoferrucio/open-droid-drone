library(zoo)
library(lattice)

options(digits.secs = 3)
###Lê os dados a partir do CSV passado
xRotationReadings <- read.zoo("/mnt/dados/Meus Documentos/Estudo/Mestrado/Repositório/trunk/Material pesquisa/Leituras de sensores/Fusão sensorial/Atitude/tiltX.csv", index.column = 1, sep = ",", header = TRUE, FUN = as.POSIXct, format='%H:%M:%OS')
#as.xts(xRotationReadings)
#leituras <- as.xts(xRotationReadings, names=c("pressão atmosférica", "temperatura ambiente", "altitude absoluta"))

###Renomea o nome das colunas
colnames(xRotationReadings) [1:4] <- c(expression("Acelerômetro X", "Acelerômetro Y", "Giroscópio X", "Giroscópio Y"))

###Imprime o gráfico de linhas
#lwd---> line width
#col---> color (red, orange, blue, blue, green and combinations with dark[nome_cor] or light[nome_cor]), to list all combinations use function colors()
#type--->"g"(grid), "l"(line), "p"(point), "r"(linear regression), "smooth"
#las--->labels are parallel (=0) or perpendicular(=2) to axis
#pch--->point character
#cex--->character extension
#lty--->line type
xyplot(xRotationReadings, superpose=TRUE, type=c("g", "l"), auto.key=list(space="top", columns=4, lines=TRUE, points=FALSE), pch=c(1:14), cex=1, lty=c(1:4), main="Rotação do eixo X", xlab="Tempo (segs)", ylab="Rotação (°)", las=2, lwd=1)

##############################################################################################
options(digits.secs = 3)
###Lê os dados a partir do CSV passado
xRotationReadingsFull <- read.zoo("/mnt/dados/Meus Documentos/Estudo/Mestrado/Repositório/trunk/Material pesquisa/Leituras de sensores/Fusão sensorial/Atitude/tiltX_full.csv", index.column = 1, sep = ",", header = TRUE, FUN = as.POSIXct, format='%H:%M:%OS')
#as.xts(xRotationReadingsFull)
#leituras <- as.xts(xRotationReadingsFull, names=c("pressão atmosférica", "temperatura ambiente", "altitude absoluta"))

###Renomea o nome das colunas
colnames(xRotationReadingsFull) [1:3] <- c(expression("Fusão X", "Giroscópio X", "Acelerômetro X"))

###Imprime o gráfico de linhas
#lwd---> line width
#col---> color (red, orange, blue, blue, green and combinations with dark[nome_cor] or light[nome_cor]), to list all combinations use function colors()
#type--->"g"(grid), "l"(line), "p"(point), "r"(linear regression), "smooth"
#las--->labels are parallel (=0) or perpendicular(=2) to axis
#pch--->point character
#cex--->character extension
#lty--->line type
xyplot(xRotationReadingsFull, superpose=TRUE, auto.key=list(space="top", columns=3, lines=TRUE, points=FALSE), type=c("g", "l"), pch=c(1:14), cex=1, lty=c(1:4), main="Rotação do eixo X", xlab="Tempo (segs)", ylab="Rotação (°)", las=2, lwd=1)

##############################################################################################
options(digits.secs = 3)
###Lê os dados a partir do CSV passado
yRotationReadings <- read.zoo("/mnt/dados/Meus Documentos/Estudo/Mestrado/Repositório/trunk/Material pesquisa/Leituras de sensores/Fusão sensorial/Atitude/tiltY.csv", index.column = 1, sep = ",", header = TRUE, FUN = as.POSIXct, format='%H:%M:%OS')
#as.xts(yRotationReadings)
#leituras <- as.xts(yRotationReadings, names=c("pressão atmosférica", "temperatura ambiente", "altitude absoluta"))

###Renomea o nome das colunas
colnames(yRotationReadings) [1:4] <- c(expression("Acelerômetro X", "Acelerômetro Y", "Giroscópio X", "Giroscópio Y"))

###Imprime o gráfico de linhas
#lwd---> line width
#col---> color (red, orange, blue, blue, green and combinations with dark[nome_cor] or light[nome_cor]), to list all combinations use function colors()
#type--->"g"(grid), "l"(line), "p"(point), "r"(linear regression), "smooth"
#las--->labels are parallel (=0) or perpendicular(=2) to axis
#pch--->point character
#cex--->character extension
#lty--->line type
xyplot(yRotationReadings, superpose=TRUE, type=c("g", "l"), pch=c(1:14), cex=1, lty=c(1:4), main="Rotação do eixo Y", xlab="Tempo (segs)", ylab="Rotação (°)", las=2, lwd=1)

##############################################################################################
options(digits.secs = 3)
###Lê os dados a partir do CSV passado
yRotationReadingsFull <- read.zoo("/mnt/dados/Meus Documentos/Estudo/Mestrado/Repositório/trunk/Material pesquisa/Leituras de sensores/Fusão sensorial/Atitude/tiltY_full.csv", index.column = 1, sep = ",", header = TRUE, FUN = as.POSIXct, format='%H:%M:%OS')
#as.xts(yRotationReadingsFull)
#leituras <- as.xts(yRotationReadingsFull, names=c("pressão atmosférica", "temperatura ambiente", "altitude absoluta"))

###Renomea o nome das colunas
colnames(yRotationReadingsFull) [1:3] <- c(expression("Fusão Y", "Giroscópio Y", "Acelerômetro Y"))

###Imprime o gráfico de linhas
#lwd---> line width
#col---> color (red, orange, blue, blue, green and combinations with dark[nome_cor] or light[nome_cor]), to list all combinations use function colors()
#type--->"g"(grid), "l"(line), "p"(point), "r"(linear regression), "smooth"
#las--->labels are parallel (=0) or perpendicular(=2) to axis
#pch--->point character
#cex--->character extension
#lty--->line type
xyplot(yRotationReadingsFull, superpose=TRUE, auto.key=list(space="top", columns=3, lines=TRUE, points=FALSE), type=c("g", "l"), pch=c(1:14), cex=1, lty=c(1:4), main="Rotação do eixo Y", xlab="Tempo (segs)", ylab="Rotação (°)", las=2, lwd=1)

##############################################################################################
options(digits.secs = 3)
###Lê os dados a partir do CSV passado
xRotationReadingsFull09 <- read.zoo("/mnt/dados/Meus Documentos/Estudo/Mestrado/Repositório/trunk/Material pesquisa/Leituras de sensores/Fusão sensorial/Atitude/selectedAlpha/quadcopterControllerLogSensory_tiltX_full_0,9.csv", index.column = 1, sep = ",", header = TRUE, FUN = as.POSIXct, format='%H:%M:%OS')
#as.xts(xRotationReadingsFull0,9)
#leituras <- as.xts(xRotationReadingsFull0,9, names=c("pressão atmosférica", "temperatura ambiente", "altitude absoluta"))

###Renomea o nome das colunas
colnames(xRotationReadingsFull09) [1:3] <- c(expression("Fusão X (α=0,9)", "Giroscópio X", "Acelerômetro X"))

###Imprime o gráfico de linhas
#lwd---> line width
#col---> color (red, orange, blue, blue, green and combinations with dark[nome_cor] or light[nome_cor]), to list all combinations use function colors()
#type--->"g"(grid), "l"(line), "p"(point), "r"(linear regression), "smooth"
#las--->labels are parallel (=0) or perpendicular(=2) to axis
#pch--->point character
#cex--->character extension
#lty--->line type
xyplot(xRotationReadingsFull09, superpose=TRUE, type=c("g", "l"), pch=c(1:14), cex=1, lty=c(1:4), main="Rotação do eixo X", xlab="Tempo (segs)", ylab="Rotação (°)", las=2, lwd=1)

##############################################################################################
options(digits.secs = 3)
###Lê os dados a partir do CSV passado
yRotationReadingsFull09 <- read.zoo("/mnt/dados/Meus Documentos/Estudo/Mestrado/Repositório/trunk/Material pesquisa/Leituras de sensores/Fusão sensorial/Atitude/selectedAlpha/quadcopterControllerLogSensory_tiltY_full_0,9.csv", index.column = 1, sep = ",", header = TRUE, FUN = as.POSIXct, format='%H:%M:%OS')
#as.xts(yRotationReadingsFull0,9)
#leituras <- as.xts(yRotationReadingsFull0,9, names=c("pressão atmosférica", "temperatura ambiente", "altitude absoluta"))

###Renomea o nome das colunas
colnames(yRotationReadingsFull09) [1:3] <- c(expression("Fusão Y (α=0,9)", "Giroscópio Y", "Acelerômetro Y"))

###Imprime o gráfico de linhas
#lwd---> line width
#col---> color (red, orange, blue, blue, green and combinations with dark[nome_cor] or light[nome_cor]), to list all combinations use function colors()
#type--->"g"(grid), "l"(line), "p"(point), "r"(linear regression), "smooth"
#las--->labels are parallel (=0) or perpendicular(=2) to axis
#pch--->point character
#cex--->character extension
#lty--->line type
xyplot(yRotationReadingsFull09, superpose=TRUE, type=c("g", "l"), pch=c(1:14), cex=1, lty=c(1:4), main="Rotação do eixo Y", xlab="Tempo (segs)", ylab="Rotação (°)", las=2, lwd=1)


##############################################################################################
options(digits.secs = 3)
###Lê os dados a partir do CSV passado
rotationReadings020 <- read.zoo("/mnt/dados/Meus Documentos/Estudo/Mestrado/Repositório/trunk/Material pesquisa/Leituras de sensores/Fusão sensorial/Atitude/selectedAlpha/quadcopterControllerLogSensory_tiltY_full_0,1.csv", index.column = 1, sep = ",", header = TRUE, FUN = as.POSIXct, format='%H:%M:%OS')
#as.xts(rotationReadings020)
#leituras <- as.xts(rotationReadings020, names=c("pressão atmosférica", "temperatura ambiente", "altitude absoluta"))

###Renomea o nome das colunas
#colnames(rotationReadings020) [1:6] <- c(expression("Fusão X (α=0,1)", "Fusão Y (α=0,1)", "Acelerômetro X", "Acelerômetro Y", "Giroscópio X", "Giroscópio Y"))
#colnames(rotationReadings020) [1:3] <- c(expression("Fusão X (α=0,1)", "Acelerômetro X", "Giroscópio X"))
colnames(rotationReadings020) [1:3] <- c(expression("Fusão Y (α=0,1)", "Acelerômetro Y", "Giroscópio Y"))

###Imprime o gráfico de linhas
#lwd---> line width
#col---> color (red, orange, blue, blue, green and combinations with dark[nome_cor] or light[nome_cor]), to list all combinations use function colors()
#type--->"g"(grid), "l"(line), "p"(point), "r"(linear regression), "smooth"
#las--->labels are parallel (=0) or perpendicular(=2) to axis
#pch--->point character
#cex--->character extension
#lty--->line type
#xyplot(rotationReadings020, superpose=TRUE, auto.key=list(space="top", columns=3, lines=TRUE, points=FALSE), type=c("g", "l"), pch=c(1:14), cex=1, lty=c(1:4), main="Rotação do eixo X", xlab="Tempo (segs)", ylab="Rotação (°)", las=2, lwd=1)
xyplot(rotationReadings020, superpose=TRUE, auto.key=list(space="top", columns=3, lines=TRUE, points=FALSE), type=c("g", "l"), pch=c(1:14), cex=1, lty=c(1:4), main="Rotação do eixo Y", xlab="Tempo (segs)", ylab="Rotação (°)", las=2, lwd=1)

##############################################################################################
options(digits.secs = 3)
###Lê os dados a partir do CSV passado
xAxisRotation <- read.zoo("/mnt/dados/Meus Documentos/Estudo/Mestrado/Repositório/trunk/Material pesquisa/Leituras de sensores/Fusão sensorial/Atitude/quadcopterControllerLogSensory_atComplementary_xAxis.csv", index.column = 1, sep = ",", header = TRUE, FUN = as.POSIXct, format='%H:%M:%OS')
#as.xts(rotationReadings020)
#leituras <- as.xts(rotationReadings020, names=c("pressão atmosférica", "temperatura ambiente", "altitude absoluta"))

###Renomea o nome das colunas
#colnames(rotationReadings020) [1:6] <- c(expression("Fusão X (α=0,1)", "Fusão Y (α=0,1)", "Acelerômetro X", "Acelerômetro Y", "Giroscópio X", "Giroscópio Y"))
#colnames(rotationReadings020) [1:3] <- c(expression("Fusão X (α=0,1)", "Acelerômetro X", "Giroscópio X"))
colnames(xAxisRotation) [1:3] <- c(expression("Fusão X (α=0,98)", "Acelerômetro X", "Giroscópio X"))

###Imprime o gráfico de linhas
#lwd---> line width
#col---> color (red, orange, blue, blue, green and combinations with dark[nome_cor] or light[nome_cor]), to list all combinations use function colors()
#type--->"g"(grid), "l"(line), "p"(point), "r"(linear regression), "smooth"
#las--->labels are parallel (=0) or perpendicular(=2) to axis
#pch--->point character
#cex--->character extension
#lty--->line type
#xyplot(rotationReadings020, superpose=TRUE, auto.key=list(space="top", columns=3, lines=TRUE, points=FALSE), type=c("g", "l"), pch=c(1:14), cex=1, lty=c(1:4), main="Rotação do eixo X", xlab="Tempo (segs)", ylab="Rotação (°)", las=2, lwd=1)
xyplot(xAxisRotation, superpose=TRUE, auto.key=list(space="top", columns=1, lines=TRUE, points=FALSE), scales=list(x=list(rot=0),y=list(rot=0)), type=c("g", "l"), pch=c(1:14), cex=1, lty=c(1:4), main="Rotação do eixo X", xlab="Tempo (segs)", ylab="Rotação (°)", las=2, lwd=1)

##############################################################################################
options(digits.secs = 3)
###Lê os dados a partir do CSV passado
yAxisRotation <- read.zoo("/mnt/dados/Meus Documentos/Estudo/Mestrado/Repositório/trunk/Material pesquisa/Leituras de sensores/Fusão sensorial/Atitude/quadcopterControllerLogSensory_atComplementary_yAxis.csv", index.column = 1, sep = ",", header = TRUE, FUN = as.POSIXct, format='%H:%M:%OS')
#as.xts(rotationReadings020)
#leituras <- as.xts(rotationReadings020, names=c("pressão atmosférica", "temperatura ambiente", "altitude absoluta"))

###Renomea o nome das colunas
#colnames(rotationReadings020) [1:6] <- c(expression("Fusão X (α=0,1)", "Fusão Y (α=0,1)", "Acelerômetro X", "Acelerômetro Y", "Giroscópio X", "Giroscópio Y"))
#colnames(rotationReadings020) [1:3] <- c(expression("Fusão X (α=0,1)", "Acelerômetro X", "Giroscópio X"))
colnames(yAxisRotation) [1:3] <- c(expression("Fusão Y (α=0,98)", "Acelerômetro Y", "Giroscópio Y"))

###Imprime o gráfico de linhas
#lwd---> line width
#col---> color (red, orange, blue, blue, green and combinations with dark[nome_cor] or light[nome_cor]), to list all combinations use function colors()
#type--->"g"(grid), "l"(line), "p"(point), "r"(linear regression), "smooth"
#las--->labels are parallel (=0) or perpendicular(=2) to axis
#pch--->point character
#cex--->character extension
#lty--->line type
#xyplot(rotationReadings020, superpose=TRUE, auto.key=list(space="top", columns=3, lines=TRUE, points=FALSE), type=c("g", "l"), pch=c(1:14), cex=1, lty=c(1:4), main="Rotação do eixo X", xlab="Tempo (segs)", ylab="Rotação (°)", las=2, lwd=1)
xyplot(yAxisRotation, superpose=TRUE, auto.key=list(space="top", columns=1, lines=TRUE, points=FALSE), scales=list(x=list(rot=0),y=list(rot=0)), type=c("g", "l"), pch=c(1:14), cex=1, lty=c(1:4), main="Rotação do eixo Y", xlab="Tempo (segs)", ylab="Rotação (°)", las=2, lwd=1)

##############################################################################################
options(digits.secs = 3)
###Lê os dados a partir do CSV passado
xAxisRotation <- read.zoo("/mnt/dados/Meus Documentos/Estudo/Mestrado/Repositório/trunk/Material pesquisa/Leituras de sensores/Fusão sensorial/Atitude/quadcopterControllerLogSensory_atComplementary_xAxis.csv", index.column = 1, sep = ",", header = TRUE, FUN = as.POSIXct, format='%H:%M:%OS')
#as.xts(rotationReadings020)
#leituras <- as.xts(rotationReadings020, names=c("pressão atmosférica", "temperatura ambiente", "altitude absoluta"))

###Renomea o nome das colunas
#colnames(rotationReadings020) [1:6] <- c(expression("Fusão X (α=0,1)", "Fusão Y (α=0,1)", "Acelerômetro X", "Acelerômetro Y", "Giroscópio X", "Giroscópio Y"))
#colnames(rotationReadings020) [1:3] <- c(expression("Fusão X (α=0,1)", "Acelerômetro X", "Giroscópio X"))
colnames(xAxisRotation) [1:3] <- c(expression("Fusion X (α=0,98)", "Accelerometer X", "Gyroscope X"))

###Imprime o gráfico de linhas
#lwd---> line width
#col---> color (red, orange, blue, blue, green and combinations with dark[nome_cor] or light[nome_cor]), to list all combinations use function colors()
#type--->"g"(grid), "l"(line), "p"(point), "r"(linear regression), "smooth"
#las--->labels are parallel (=0) or perpendicular(=2) to axis
#pch--->point character
#cex--->character extension
#lty--->line type
#xyplot(rotationReadings020, superpose=TRUE, auto.key=list(space="top", columns=3, lines=TRUE, points=FALSE), type=c("g", "l"), pch=c(1:14), cex=1, lty=c(1:4), main="Rotação do eixo X", xlab="Tempo (segs)", ylab="Rotação (°)", las=2, lwd=1)
xyplot(xAxisRotation, superpose=TRUE, auto.key=list(space="top", columns=1, lines=TRUE, points=FALSE), scales=list(x=list(rot=0),y=list(rot=0)), type=c("g", "l"), pch=c(1:14), cex=1, lty=c(1:4), main="X axis rotation", xlab="Time (secs)", ylab="Rotation (°)", las=2, lwd=1)

##############################################################################################
options(digits.secs = 3)
###Lê os dados a partir do CSV passado
yAxisRotation <- read.zoo("/mnt/dados/Meus Documentos/Estudo/Mestrado/Repositório/trunk/Material pesquisa/Leituras de sensores/Fusão sensorial/Atitude/quadcopterControllerLogSensory_atComplementary_yAxis.csv", index.column = 1, sep = ",", header = TRUE, FUN = as.POSIXct, format='%H:%M:%OS')
#as.xts(rotationReadings020)
#leituras <- as.xts(rotationReadings020, names=c("pressão atmosférica", "temperatura ambiente", "altitude absoluta"))

###Renomea o nome das colunas
#colnames(rotationReadings020) [1:6] <- c(expression("Fusão X (α=0,1)", "Fusão Y (α=0,1)", "Acelerômetro X", "Acelerômetro Y", "Giroscópio X", "Giroscópio Y"))
#colnames(rotationReadings020) [1:3] <- c(expression("Fusão X (α=0,1)", "Acelerômetro X", "Giroscópio X"))
colnames(yAxisRotation) [1:3] <- c(expression("Fusion Y (α=0,98)", "Accelerometer Y", "Gyroscope Y"))

###Imprime o gráfico de linhas
#lwd---> line width
#col---> color (red, orange, blue, blue, green and combinations with dark[nome_cor] or light[nome_cor]), to list all combinations use function colors()
#type--->"g"(grid), "l"(line), "p"(point), "r"(linear regression), "smooth"
#las--->labels are parallel (=0) or perpendicular(=2) to axis
#pch--->point character
#cex--->character extension
#lty--->line type
#xyplot(rotationReadings020, superpose=TRUE, auto.key=list(space="top", columns=3, lines=TRUE, points=FALSE), type=c("g", "l"), pch=c(1:14), cex=1, lty=c(1:4), main="Rotação do eixo X", xlab="Tempo (segs)", ylab="Rotação (°)", las=2, lwd=1)
xyplot(yAxisRotation, superpose=TRUE, auto.key=list(space="top", columns=1, lines=TRUE, points=FALSE), scales=list(x=list(rot=0),y=list(rot=0)), type=c("g", "l"), pch=c(1:14), cex=1, lty=c(1:4), main="Y axis rotation", xlab="Time (secs)", ylab="Rotation (°)", las=2, lwd=1)

##############################################################################################
options(digits.secs = 3)
###Lê os dados a partir do CSV passado
teste <- read.zoo("/mnt/dados/Meus Documentos/Estudo/Mestrado/Repositório/trunk/Material pesquisa/Leituras de sensores/Fusão sensorial/Atitude/teste.csv", index.column = 1, sep = ",", header = TRUE, FUN = as.POSIXct, format='%H:%M:%OS')
#as.xts(rotationReadings020)
#leituras <- as.xts(rotationReadings020, names=c("pressão atmosférica", "temperatura ambiente", "altitude absoluta"))

###Renomea o nome das colunas
#colnames(rotationReadings020) [1:6] <- c(expression("Fusão X (α=0,1)", "Fusão Y (α=0,1)", "Acelerômetro X", "Acelerômetro Y", "Giroscópio X", "Giroscópio Y"))
#colnames(rotationReadings020) [1:3] <- c(expression("Fusão X (α=0,1)", "Acelerômetro X", "Giroscópio X"))
colnames(teste) [1:3] <- c(expression("Fusão X (α=0,98)", "Acelerômetro X", "Giroscópio X"))

###Imprime o gráfico de linhas
#lwd---> line width
#col---> color (red, orange, blue, blue, green and combinations with dark[nome_cor] or light[nome_cor]), to list all combinations use function colors()
#type--->"g"(grid), "l"(line), "p"(point), "r"(linear regression), "smooth"
#las--->labels are parallel (=0) or perpendicular(=2) to axis
#pch--->point character
#cex--->character extension
#lty--->line type
#xyplot(rotationReadings020, superpose=TRUE, auto.key=list(space="top", columns=3, lines=TRUE, points=FALSE), type=c("g", "l"), pch=c(1:14), cex=1, lty=c(1:4), main="Rotação do eixo X", xlab="Tempo (segs)", ylab="Rotação (°)", las=2, lwd=1)
xyplot(teste, superpose=TRUE, auto.key=list(space="top", columns=3, lines=TRUE, points=FALSE), type=c("g", "l"), pch=c(1:14), cex=1, lty=c(1:4), main="Rotação do eixo X", xlab="Tempo (segs)", ylab="Rotação (°)", las=2, lwd=1)

###Executar até antes desse comentário, depois de salvar todas as imagens executar o reset de par()

###Reseta os valores de par() apagando todas as fotos do RStudio
dev.off()
