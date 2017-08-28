library(zoo)
library(lattice)

options(digits.secs = 3)
###Lê os dados a partir do CSV passado
barometerReadings <- read.zoo("/mnt/dados/Meus Documentos/Estudo/Mestrado/Repositório/trunk/Material pesquisa/Leituras de sensores/Sem filtro/Barômetro/barometer_measuredAtHome'sDinnerTable.csv", index.column = 1, sep = ",", header = TRUE, FUN = as.POSIXct, format='%Y-%m-%d %H:%M:%OS')
#as.xts(barometerReadings)
#leituras <- as.xts(barometerReadings, names=c("pressão atmosférica", "temperatura ambiente", "altitude absoluta"))

###Renomea o nome das colunas
colnames(barometerReadings) [1:3] <- c("Pressão atmosférica [hPa]", "Temperatura ambiente [°C]", "Altitude absoluta [m]")

###Imprime o gráfico de linhas
#lwd---> line width
#col---> color (red, orange, blue, blue, green and combinations with dark[nome_cor] or light[nome_cor]), to list all combinations use function colors()
#type--->"g"(grid), "l"(line), "p"(point), "r"(linear regression), "smooth"
#las--->labels are parallel (=0) or perpendicular(=2) to axis
xyplot(barometerReadings, superpose=FALSE, type=c("g", "l"), scales=list(x=list(rot=0),y=list(rot=0)), main="Barômetro estático", xlab="Tempo (hh:mm)", ylab="Medidas", las=2, lwd=1)

par(mfrow=c(1,3), oma=c(0, 0, 2, 0))
boxplot(coredata(barometerReadings$"Pressão atmosférica [hPa]"), main="Pressão atmosférica", ylab="Pressão atm. (hPa)", las=2)
boxplot(coredata(barometerReadings$"Temperatura ambiente [°C]"), main="Temperatura ambiente",  ylab="Temp. ambiente (°C)", las=2)
boxplot(coredata(barometerReadings$"Altitude absoluta [m]"), main="Altitude absoluta", ylab="Altitude abs. (m)", las=2)
###Imprime um título para o conjunto de gráficos
#cex--->character extension, it means characters size
mtext("Barômetro estático", outer = TRUE, cex = 1.3)

#Estatísticas descritivas
summary(barometerReadings$"Pressão atmosférica [hPa]")
#Desvio padrão
sd(barometerReadings$"Pressão atmosférica [hPa]", na.rm = FALSE)
#Estatísticas descritivas
summary(barometerReadings$"Temperatura ambiente [°C]")
#Desvio padrão
sd(barometerReadings$"Temperatura ambiente [°C]", na.rm = FALSE)
#Estatísticas descritivas
summary(barometerReadings$"Altitude absoluta [m]")
#Desvio padrão
sd(barometerReadings$"Altitude absoluta [m]", na.rm = FALSE)

###Executar até antes desse comentário, depois de salvar todas as imagens executar o reset de par()

###Reseta os valores de par() apagando todas as fotos do RStudio
dev.off()

################################################################################
options(digits.secs = 3)
###Lê os dados a partir do CSV passado
barometerReadingsOscillating <- read.zoo("/mnt/dados/Meus Documentos/Estudo/Mestrado/Repositório/trunk/Material pesquisa/Leituras de sensores/Sem filtro/Barômetro/barometer_oscillating.csv", index.column = 1, sep = ",", header = TRUE, FUN = as.POSIXct, format = '%H:%M:%OS')
#as.xts(barometerReadingsOscillating)
#leituras <- as.xts(barometerReadingsOscillating, names=c("pressão atmosférica", "temperatura ambiente", "altitude absoluta"))

###Renomea o nome das colunas
colnames(barometerReadingsOscillating) [1:3] <- c("Pressão atmosférica [hPa]", "Temperatura ambiente [°C]", "Altitude absoluta [m]")

###Imprime o gráfico de linhas
#lwd---> line width
#col---> color (red, orange, blue, blue, green and combinations with dark[nome_cor] or light[nome_cor]), to list all combinations use function colors()
#type--->"g"(grid), "l"(line), "p"(point), "r"(linear regression), "smooth"
#las--->labels are parallel (=0) or perpendicular(=2) to axis
xyplot(barometerReadingsOscillating, superpose=FALSE, type=c("g", "l"), scales=list(x=list(rot=0),y=list(rot=0)), pch=c(1:14), cex=1, lty=c(1), main="Barômetro em movimento", xlab="Tempo (hh:mm)", ylab="Medidas", las=2, lwd=1)


#par(mfrow=c(1,3))
#boxplot(coredata(barometerReadingsOscillating$"Eixo X"),  ylab="Velocidade angular (°/s)", las=0)
#boxplot(coredata(barometerReadingsOscillating$"Eixo Y"), main="Acelerômetro oscilando",  ylab="Velocidade angular (°/s)", las=0)
#boxplot(coredata(barometerReadingsOscillating$"Eixo Z"), ylab="Velocidade angular (°/s)", las=0)

###Executar até antes desse comentário, depois de salvar todas as imagens executar o reset de par()

###Reseta os valores de par() apagando todas as fotos do RStudio
#dev.off()

