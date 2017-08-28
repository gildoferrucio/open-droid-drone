library(zoo)
library(lattice)

options(digits.secs = 3)
###Lê os dados a partir do CSV passado
accelerometerReadingsStatic <- read.zoo("/mnt/dados/Meus Documentos/Estudo/Mestrado/Repositório/trunk/Material pesquisa/Leituras de sensores/Sem filtro/Acelerômetro/accelerometer_static.csv", index.column = 1, sep = ",", header = TRUE, FUN = as.POSIXct, format='%Y-%m-%d %H:%M:%OS')
#as.xts(accelerometerReadingsStatic)
#leituras <- as.xts(accelerometerReadingsStatic, names=c("pressão atmosférica", "temperatura ambiente", "altitude absoluta"))

###Renomea o nome das colunas
colnames(accelerometerReadingsStatic) [1:3] <- c("Eixo X", "Eixo Y", "Eixo Z")

###Imprime o gráfico de linhas
#lwd---> line width
#col---> color (red, orange, blue, blue, green and combinations with dark[nome_cor] or light[nome_cor]), to list all combinations use function colors()
#type--->"g"(grid), "l"(line), "p"(point), "r"(linear regression), "smooth"
#las--->labels are parallel (=0) or perpendicular(=2) to axis
xyplot(accelerometerReadingsStatic, superpose=FALSE, type=c("g", "l"), scales=list(x=list(rot=0),y=list(rot=0)), pch=c(1:14), cex=1, lty=c(1:1), main="Acelerômetro estático", xlab="Tempo (hh:mm)", ylab="Aceleração (m/s²)", las=2, lwd=1)

par(mfrow=c(1,3), oma=c(0, 0, 2, 0))
boxplot(coredata(accelerometerReadingsStatic$"Eixo X"), main="Eixo X", ylab="Aceleração (m/s²)", las=2)
boxplot(coredata(accelerometerReadingsStatic$"Eixo Y"), main="Eixo Y", ylab="Aceleração (m/s²)", las=2)
boxplot(coredata(accelerometerReadingsStatic$"Eixo Z"), main="Eixo Z", ylab="Aceleração (m/s²)", las=2)
###Imprime um título para o conjunto de gráficos
#cex--->character extension, it means characters size
mtext("Acelerômetro estático", outer = TRUE, cex = 1.3)

#boxplot(coredata(accelerometerReadingsStatic), main="Acelerômetro estático", ylab="Aceleração (m/s²)", las=0)

#Estatísticas descritivas
summary(accelerometerReadingsStatic$"Eixo X")
#Desvio padrão
sd(accelerometerReadingsStatic$"Eixo X", na.rm = FALSE)
#Estatísticas descritivas
summary(accelerometerReadingsStatic$"Eixo Y")
#Desvio padrão
sd(accelerometerReadingsStatic$"Eixo Y", na.rm = FALSE)
#Estatísticas descritivas
summary(accelerometerReadingsStatic$"Eixo Z")
#Desvio padrão
sd(accelerometerReadingsStatic$"Eixo Z", na.rm = FALSE)

###Executar até antes desse comentário, depois de salvar todas as imagens executar o reset de par()

###Reseta os valores de par() apagando todas as fotos do RStudio
dev.off()

################################################################################
options(digits.secs = 3)
###Lê os dados a partir do CSV passado
accelerometerReadingsOscillating <- read.zoo("/mnt/dados/Meus Documentos/Estudo/Mestrado/Repositório/trunk/Material pesquisa/Leituras de sensores/Sem filtro/Acelerômetro/accelerometer_oscillating_90degrees.csv", index.column = 1, sep = ",", header = TRUE, FUN = as.POSIXct, format='%Y-%m-%d %H:%M:%OS')
#as.xts(accelerometerReadingsOscillating)
#leituras <- as.xts(accelerometerReadingsOscillating, names=c("pressão atmosférica", "temperatura ambiente", "altitude absoluta"))

###Renomea o nome das colunas
colnames(accelerometerReadingsOscillating) [1:3] <- c("Eixo X", "Eixo Y", "Eixo Z")

###Imprime o gráfico de linhas
#lwd---> line width
#col---> color (red, orange, blue, blue, green and combinations with dark[nome_cor] or light[nome_cor]), to list all combinations use function colors()
#type--->"g"(grid), "l"(line), "p"(point), "r"(linear regression), "smooth"
#las--->labels are parallel (=0) or perpendicular(=2) to axis
xyplot(accelerometerReadingsOscillating, superpose=FALSE, type=c("g", "l"), scales=list(x=list(rot=0),y=list(rot=0)), pch=c(1:14), cex=1, lty=c(1:1), main="Acelerômetro em movimento", xlab="Tempo (hh:mm)", ylab="Aceleração (m/s²)", las=2, lwd=1)


#par(mfrow=c(1,3))
#boxplot(coredata(accelerometerReadingsOscillating$"Eixo X"),  ylab="Velocidade angular (°/s)", las=0)
#boxplot(coredata(accelerometerReadingsOscillating$"Eixo Y"), main="Acelerômetro oscilando",  ylab="Velocidade angular (°/s)", las=0)
#boxplot(coredata(accelerometerReadingsOscillating$"Eixo Z"), ylab="Velocidade angular (°/s)", las=0)

###Executar até antes desse comentário, depois de salvar todas as imagens executar o reset de par()

###Reseta os valores de par() apagando todas as fotos do RStudio
#dev.off()
