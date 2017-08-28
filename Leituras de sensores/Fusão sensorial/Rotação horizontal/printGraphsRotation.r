library(zoo)
library(lattice)

options(digits.secs = 3)
###Lê os dados a partir do CSV passado
zAxisRotation098 <- read.zoo("/mnt/dados/Meus Documentos/Estudo/Mestrado/Repositório/trunk/Material pesquisa/Leituras de sensores/Fusão sensorial/Rotação horizontal/quadcopterControllerLogSensory_hrComplementary_0,98.csv", index.column = 1, sep = ",", header = TRUE, FUN = as.POSIXct, format='%H:%M:%OS')
#as.xts(zRotationReadingsFull)
#leituras <- as.xts(zRotationReadingsFull, names=c("pressão atmosférica", "temperatura ambiente", "altitude absoluta"))

###Renomea o nome das colunas
colnames(zAxisRotation098) [1:3] <- c(expression("Fusão Z (α=0,98)", "Magnetômetro Z", "Giroscópio Z"))

###Imprime o gráfico de linhas
#lwd---> line width
#col---> color (red, orange, blue, blue, green and combinations with dark[nome_cor] or light[nome_cor]), to list all combinations use function colors()
#type--->"g"(grid), "l"(line), "p"(point), "r"(linear regression), "smooth"
#las--->labels are parallel (=0) or perpendicular(=2) to axis
#pch--->point character
#cex--->character extension
#lty--->line type
xyplot(zAxisRotation098, superpose=TRUE, auto.key=list(space="top", columns=1, lines=TRUE, points=FALSE), scales=list(x=list(rot=0),y=list(rot=0)), type=c("g", "l"), pch=c(1:14), cex=1, lty=c(1:4), main="Rotação do eixo Z", xlab="Tempo (segs)", ylab="Rotação (°)", las=2, lwd=1)

##############################################################################################
options(digits.secs = 3)
###Lê os dados a partir do CSV passado
zAxisRotation09925 <- read.zoo("/mnt/dados/Meus Documentos/Estudo/Mestrado/Repositório/trunk/Material pesquisa/Leituras de sensores/Fusão sensorial/Rotação horizontal/quadcopterControllerLogSensory_hrComplementary_0,9925.csv", index.column = 1, sep = ",", header = TRUE, FUN = as.POSIXct, format='%H:%M:%OS')
#as.xts(zRotationReadingsFull)
#leituras <- as.xts(zRotationReadingsFull, names=c("pressão atmosférica", "temperatura ambiente", "altitude absoluta"))

###Renomea o nome das colunas
colnames(zAxisRotation09925) [1:3] <- c(expression("Fusão Z (α=0,9925)", "Magnetômetro Z", "Giroscópio Z"))

###Imprime o gráfico de linhas
#lwd---> line width
#col---> color (red, orange, blue, blue, green and combinations with dark[nome_cor] or light[nome_cor]), to list all combinations use function colors()
#type--->"g"(grid), "l"(line), "p"(point), "r"(linear regression), "smooth"
#las--->labels are parallel (=0) or perpendicular(=2) to axis
#pch--->point character
#cex--->character extension
#lty--->line type
xyplot(zAxisRotation09925, superpose=TRUE, auto.key=list(space="top", columns=1, lines=TRUE, points=FALSE), scales=list(x=list(rot=0),y=list(rot=0)), type=c("g", "l"), pch=c(1:14), cex=1, lty=c(1:4), main="Rotação do eixo Z", xlab="Tempo (segs)", ylab="Rotação (°)", las=2, lwd=1)

##############################################################################################
options(digits.secs = 3)
###Lê os dados a partir do CSV passado
zAxisRotation098 <- read.zoo("/mnt/dados/Meus Documentos/Estudo/Mestrado/Repositório/trunk/Material pesquisa/Leituras de sensores/Fusão sensorial/Rotação horizontal/quadcopterControllerLogSensory_hrComplementary_0,98.csv", index.column = 1, sep = ",", header = TRUE, FUN = as.POSIXct, format='%H:%M:%OS')
#as.xts(zRotationReadingsFull)
#leituras <- as.xts(zRotationReadingsFull, names=c("pressão atmosférica", "temperatura ambiente", "altitude absoluta"))

###Renomea o nome das colunas
colnames(zAxisRotation098) [1:3] <- c(expression("Fusion Z (α=0,98)", "Magnetometer Z", "Gyroscope Z"))

###Imprime o gráfico de linhas
#lwd---> line width
#col---> color (red, orange, blue, blue, green and combinations with dark[nome_cor] or light[nome_cor]), to list all combinations use function colors()
#type--->"g"(grid), "l"(line), "p"(point), "r"(linear regression), "smooth"
#las--->labels are parallel (=0) or perpendicular(=2) to axis
#pch--->point character
#cex--->character extension
#lty--->line type
xyplot(zAxisRotation098, superpose=TRUE, auto.key=list(space="top", columns=1, lines=TRUE, points=FALSE), scales=list(x=list(rot=0),y=list(rot=0)), type=c("g", "l"), pch=c(1:14), cex=1, lty=c(1:4), main="Z axis rotation", xlab="Time (secs)", ylab="Rotation (°)", las=2, lwd=1)

##############################################################################################
options(digits.secs = 3)
###Lê os dados a partir do CSV passado
zAxisRotation09925 <- read.zoo("/mnt/dados/Meus Documentos/Estudo/Mestrado/Repositório/trunk/Material pesquisa/Leituras de sensores/Fusão sensorial/Rotação horizontal/quadcopterControllerLogSensory_hrComplementary_0,9925.csv", index.column = 1, sep = ",", header = TRUE, FUN = as.POSIXct, format='%H:%M:%OS')
#as.xts(zRotationReadingsFull)
#leituras <- as.xts(zRotationReadingsFull, names=c("pressão atmosférica", "temperatura ambiente", "altitude absoluta"))

###Renomea o nome das colunas
colnames(zAxisRotation09925) [1:3] <- c(expression("Fusion Z (α=0,9925)", "Magnetometer Z", "Gyroscope Z"))

###Imprime o gráfico de linhas
#lwd---> line width
#col---> color (red, orange, blue, blue, green and combinations with dark[nome_cor] or light[nome_cor]), to list all combinations use function colors()
#type--->"g"(grid), "l"(line), "p"(point), "r"(linear regression), "smooth"
#las--->labels are parallel (=0) or perpendicular(=2) to axis
#pch--->point character
#cex--->character extension
#lty--->line type
xyplot(zAxisRotation09925, superpose=TRUE, auto.key=list(space="top", columns=1, lines=TRUE, points=FALSE), scales=list(x=list(rot=0),y=list(rot=0)), type=c("g", "l"), pch=c(1:14), cex=1, lty=c(1:4), main="Z axis rotation", xlab="Time (secs)", ylab="Rotation (°)", las=2, lwd=1)
###Executar até antes desse comentário, depois de salvar todas as imagens executar o reset de par()

###Reseta os valores de par() apagando todas as fotos do RStudio
dev.off()
