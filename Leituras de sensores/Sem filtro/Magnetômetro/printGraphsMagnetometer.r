library(zoo)
library(lattice)

options(digits.secs = 3)
###Lê os dados a partir do CSV passado
magnetometerReadingsStatic <- read.zoo("/mnt/dados/Meus Documentos/Estudo/Mestrado/Repositório/trunk/Material pesquisa/Leituras de sensores/Sem filtro/Magnetômetro/magnetometer_static.csv", index.column = 1, sep = ",", header = TRUE, FUN = as.POSIXct, format='%Y-%m-%d %H:%M:%OS')
#as.xts(magnetometerReadingsStatic)
#leituras <- as.xts(magnetometerReadingsStatic, names=c("pressão atmosférica", "temperatura ambiente", "altitude absoluta"))

###Renomea o nome das colunas
colnames(magnetometerReadingsStatic) [1:4] <- c("Eixo X [µT]", "Eixo Y [µT]", "Eixo Z [µT]", "Direção [°]")

###Imprime o gráfico de linhas
#lwd---> line width
#col---> color (red, orange, blue, blue, green and combinations with dark[nome_cor] or light[nome_cor]), to list all combinations use function colors()
#type--->"g"(grid), "l"(line), "p"(point), "r"(linear regression), "smooth"
#las--->labels are parallel (=0) or perpendicular(=2) to axis
xyplot(magnetometerReadingsStatic, superpose=FALSE, type=c("g", "l"), scales=list(x=list(rot=0),y=list(rot=0)), pch=c(1:14), cex=1, lty=c(1), main="Magnetômetro estático", xlab="Tempo (hh:mm)", ylab="Medidas", las=2, lwd=1)
#xyplot(cbind(magnetometerReadingsStatic$"Eixo X [µT]", magnetometerReadingsStatic$"Eixo Y [µT]", magnetometerReadingsStatic$"Eixo Z [µT]"), superpose=TRUE, type=c("g", "l"), pch=c(1:14), cex=1, lty=c(1:4), main="Magnetômetro estático", xlab="Tempo (hh:mm)", ylab="Medidas", las=2, lwd=1)
#xyplot(magnetometerReadingsStatic$"Direção [°]", superpose=FALSE, type=c("g", "l"), scales=list(x=list(rot=0),y=list(rot=0)), pch=c(1:14), cex=1, lty=c(1:4), main="Magnetômetro estático", xlab="Tempo (hh:mm)", ylab="Medidas", las=2, lwd=1)

par(mfrow=c(2,2), oma=c(0,0,2,0))
boxplot(coredata(magnetometerReadingsStatic$"Eixo X [µT]"), main="Eixo X", ylab="Campo mag. (µT)", las=2)
boxplot(coredata(magnetometerReadingsStatic$"Eixo Y [µT]"), main="Eixo Y", ylab="Campo mag. (µT)", las=2)
boxplot(coredata(magnetometerReadingsStatic$"Eixo Z [µT]"), main="Eixo Z", ylab="Campo mag. (µT)", las=2)
boxplot(coredata(magnetometerReadingsStatic$"Direção [°]"), main="Direção da bússola", ylab="Direção (°)", las=2)

###Imprime um título para o conjunto de gráficos
#cex--->character extension, it means characters size
mtext("Magnetômetro estático", outer = TRUE, cex = 1.3)

#boxplot(coredata(magnetometerReadingsStatic), main="Magnetômetro estático", ylab="Campo mag. (µT)", las=0)

#Estatísticas descritivas
summary(magnetometerReadingsStatic$"Eixo X [µT]")
#Desvio padrão
sd(magnetometerReadingsStatic$"Eixo X [µT]", na.rm = FALSE)
#Estatísticas descritivas
summary(magnetometerReadingsStatic$"Eixo Y [µT]")
#Desvio padrão
sd(magnetometerReadingsStatic$"Eixo Y [µT]", na.rm = FALSE)
#Estatísticas descritivas
summary(magnetometerReadingsStatic$"Eixo Z [µT]")
#Desvio padrão
sd(magnetometerReadingsStatic$"Eixo Z [µT]", na.rm = FALSE)
#Estatísticas descritivas
summary(magnetometerReadingsStatic$"Direção [°]")
#Desvio padrão
sd(magnetometerReadingsStatic$"Direção [°]", na.rm = FALSE)

###Executar até antes desse comentário, depois de salvar todas as imagens executar o reset de par()

###Reseta os valores de par() apagando todas as fotos do RStudio
dev.off()

################################################################################
options(digits.secs = 3)
###Lê os dados a partir do CSV passado
magnetometerReadingsOscillating <- read.zoo("/mnt/dados/Meus Documentos/Estudo/Mestrado/Repositório/trunk/Material pesquisa/Leituras de sensores/Sem filtro/Magnetômetro/magnetometer_oscillating_90degrees.csv", index.column = 1, sep = ",", header = TRUE, FUN = as.POSIXct, format='%Y-%m-%d %H:%M:%OS')
#as.xts(magnetometerReadingsOscillating)
#leituras <- as.xts(magnetometerReadingsOscillating, names=c("pressão atmosférica", "temperatura ambiente", "altitude absoluta"))

###Renomea o nome das colunas
colnames(magnetometerReadingsOscillating) [1:4] <- c("Eixo X [µT]", "Eixo Y [µT]", "Eixo Z [µT]", "Direção [°]")

###Imprime o gráfico de linhas
#lwd---> line width
#col---> color (red, orange, blue, blue, green and combinations with dark[nome_cor] or light[nome_cor]), to list all combinations use function colors()
#type--->"g"(grid), "l"(line), "p"(point), "r"(linear regression), "smooth"
#las--->labels are parallel (=0) or perpendicular(=2) to axis
xyplot(magnetometerReadingsOscillating, superpose=FALSE, type=c("g", "l"), scales=list(x=list(rot=0),y=list(rot=0)), main="Magnetômetro em movimento", xlab="Tempo (hh:mm)", ylab="Medidas", las=2, lwd=1)

#par(mfrow=c(1,3))
#boxplot(coredata(magnetometerReadingsOscillating$"Eixo X"),  ylab="Velocidade angular (°/s)", las=0)
#boxplot(coredata(magnetometerReadingsOscillating$"Eixo Y"), main="Giroscópio oscilando",  ylab="Velocidade angular (°/s)", las=0)
#boxplot(coredata(magnetometerReadingsOscillating$"Eixo Z"), ylab="Velocidade angular (°/s)", las=0)

###Executar até antes desse comentário, depois de salvar todas as imagens executar o reset de par()

###Reseta os valores de par() apagando todas as fotos do RStudio
#dev.off()
