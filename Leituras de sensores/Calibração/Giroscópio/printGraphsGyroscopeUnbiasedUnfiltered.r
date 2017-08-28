library(zoo)
library(lattice)

options(digits.secs = 3)
###Lê os dados a partir do CSV passado
gyroscopeReadingsStatic <- read.zoo("/mnt/dados/Meus Documentos/Estudo/Mestrado/Repositório/trunk/Material pesquisa/Leituras de sensores/Calibração/Giroscópio/quadcopterControllerLogDeviceTests_gyroscopeUnbiasedUnfiltered.csv", index.column = 1, sep = ",", header = TRUE, FUN = as.POSIXct, format='%H:%M:%OS')
#as.xts(gyroscopeReadingsStatic)
#leituras <- as.xts(gyroscopeReadingsStatic, names=c("pressão atmosférica", "temperatura ambiente", "altitude absoluta"))

###Renomea o nome das colunas
colnames(gyroscopeReadingsStatic) [1:3] <- c("Eixo X", "Eixo Y", "Eixo Z")

###Imprime o gráfico de linhas
#lwd---> line width
#col---> color (red, orange, blue, blue, green and combinations with dark[nome_cor] or light[nome_cor]), to list all combinations use function colors()
#type--->"g"(grid), "l"(line), "p"(point), "r"(linear regression), "smooth"
#las--->labels are parallel (=0) or perpendicular(=2) to axis
#xyplot(gyroscopeReadingsStatic, superpose=FALSE, type=c("g", "l"), pch=c(1:14), cex=1, lty=c(1:4), main="Giroscópio estático sem viés", xlab="Tempo (hh:mm)", ylab="Velocidade angular (°/s)", las=2, lwd=1)
xyplot(gyroscopeReadingsStatic, superpose=FALSE, type=c("g", "l"), pch=c(1:14), cex=1, scales=list(x=list(rot=0),y=list(rot=0)), main="Giroscópio estático sem viés", xlab="Tempo (hh:mm)", ylab="Velocidade angular (°/s)", las=2, lwd=1)

par(mfrow=c(1,3), oma=c(0, 0, 2, 0))
boxplot(coredata(gyroscopeReadingsStatic$"Eixo X"), main="Eixo X", ylab="Vel. angular (°/s)", las=2)
boxplot(coredata(gyroscopeReadingsStatic$"Eixo Y"), main="Eixo Y", ylab="Vel. angular (°/s)", las=2)
boxplot(coredata(gyroscopeReadingsStatic$"Eixo Z"), main="Eixo Z", ylab="Vel. angular (°/s)", las=2)
###Imprime um título para o conjunto de gráficos
#cex--->character extension, it means characters size
mtext("Giroscópio estático sem viés", outer = TRUE, cex = 1.3)

#boxplot(coredata(gyroscopeReadingsStatic), main="Giroscópio estático sem viés", ylab="Vel. angular (°/s)", las=0)

#Estatísticas descritivas
summary(gyroscopeReadingsStatic$"Eixo X")
#Desvio padrão
sd(gyroscopeReadingsStatic$"Eixo X", na.rm = FALSE)
#Estatísticas descritivas
summary(gyroscopeReadingsStatic$"Eixo Y")
#Desvio padrão
sd(gyroscopeReadingsStatic$"Eixo Y", na.rm = FALSE)
#Estatísticas descritivas
summary(gyroscopeReadingsStatic$"Eixo Z")
#Desvio padrão
sd(gyroscopeReadingsStatic$"Eixo Z", na.rm = FALSE)

###Executar até antes desse comentário, depois de salvar todas as imagens executar o reset de par()

###Reseta os valores de par() apagando todas as fotos do RStudio
dev.off()

################################################################################
options(digits.secs = 3)
###Lê os dados a partir do CSV passado
gyroscopeReadingsOscillating <- read.zoo("/mnt/dados/Meus Documentos/Estudo/Mestrado/Repositório/trunk/Material pesquisa/Leituras de sensores/Calibração do giroscópio/quadcopterControllerLogDeviceTests_gyroscopeUnbiasedUnfiltered_movement.csv", index.column = 1, sep = ",", header = TRUE, FUN = as.POSIXct, format='%H:%M:%OS')
#as.xts(gyroscopeReadingsOscillating)
#leituras <- as.xts(gyroscopeReadingsOscillating, names=c("pressão atmosférica", "temperatura ambiente", "altitude absoluta"))

###Renomea o nome das colunas
colnames(gyroscopeReadingsOscillating) [1:3] <- c("Eixo X", "Eixo Y", "Eixo Z")

###Imprime o gráfico de linhas
#lwd---> line width
#col---> color (red, orange, blue, blue, green and combinations with dark[nome_cor] or light[nome_cor]), to list all combinations use function colors()
#type--->"g"(grid), "l"(line), "p"(point), "r"(linear regression), "smooth"
#las--->labels are parallel (=0) or perpendicular(=2) to axis
xyplot(gyroscopeReadingsOscillating, superpose=FALSE, type=c("g", "l"), pch=c(1:14), cex=1, lty=c(1:4), scales=list(x=list(rot=0),y=list(rot=0)), main="Giroscópio em movimento", xlab="Tempo (hh:mm)", ylab="Velocidade angular (°/s)", las=2, lwd=1)


#par(mfrow=c(1,3))
#boxplot(coredata(gyroscopeReadingsOscillating$"Eixo X"),  ylab="Velocidade angular (°/s)", las=0)
#boxplot(coredata(gyroscopeReadingsOscillating$"Eixo Y"), main="Giroscópio oscilando",  ylab="Velocidade angular (°/s)", las=0)
#boxplot(coredata(gyroscopeReadingsOscillating$"Eixo Z"), ylab="Velocidade angular (°/s)", las=0)

###Executar até antes desse comentário, depois de salvar todas as imagens executar o reset de par()

###Reseta os valores de par() apagando todas as fotos do RStudio
#dev.off()
