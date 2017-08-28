library(zoo)
library(lattice)

options(digits.secs=3)
###Lê os dados a partir do CSV passado
rotorsCalibration <- read.zoo("/mnt/dados/Meus Documentos/Estudo/Mestrado/Repositório/trunk/Material pesquisa/Leituras de sensores/Calibração/Giroscópio/quadcopterControllerGyroscopeCalibration.csv", index.column=0, sep=",", header=TRUE, FUN=as.numeric)
#as.xts(rotorsCalibration)
#leituras <- as.xts(rotorsCalibration, names=c("pressão atmosférica", "temperatura ambiente", "altitude absoluta"))

###Renomea o nome das colunas
colnames(rotorsCalibration) [1:3] <- c("Eixo X", "Eixo Y", "Eixo Z")

###Imprime o gráfico de linhas
#lwd---> line width
#col---> color (red, orange, blue, blue, green and combinations with dark[nome_cor] or light[nome_cor]), to list all combinations use function colors()
#type--->"g"(grid), "l"(line), "p"(point), "r"(linear regression), "smooth"
#las--->labels are parallel (=0) or perpendicular(=2) to axis
#pch--->point character
#cex--->character extension
#lty--->line type
xyplot(rotorsCalibration, superpose=FALSE, type=c("g", "l"), pch=c(1:14), cex=1, scales=list(x=list(rot=0),y=list(rot=0)), main="Calibração do giroscópio", xlab="Número da amostra", ylab="angular. Vel (°/s)", las=2, lwd=1)

par(mfrow=c(1,3), oma=c(0, 0, 2, 0))
boxplot(coredata(rotorsCalibration$"Eixo X"), main="Eixo X", ylab="Vel. angular (°/s)", las=2)
boxplot(coredata(rotorsCalibration$"Eixo Y"), main="Eixo Y", ylab="Vel. angular (°/s)", las=2)
boxplot(coredata(rotorsCalibration$"Eixo Z"), main="Eixo Z", ylab="Vel. angular (°/s)", las=2)
###Imprime um título para o conjunto de gráficos
#cex--->character extension, it means characters size
mtext("Calibração do giroscópio", outer=TRUE, cex=1.3)

#Estatísticas descritivas
summary(rotorsCalibration$"Eixo X")
#Desvio padrão
sd(rotorsCalibration$"Eixo X", na.rm = FALSE)
#Estatísticas descritivas
summary(rotorsCalibration$"Eixo Y")
#Desvio padrão
sd(rotorsCalibration$"Eixo Y", na.rm = FALSE)
#Estatísticas descritivas
summary(rotorsCalibration$"Eixo Z")
#Desvio padrão
sd(rotorsCalibration$"Eixo Z", na.rm = FALSE)

###Executar até antes desse comentário, depois de salvar todas as imagens executar o reset de par()

###Reseta os valores de par() apagando todas as fotos do RStudio
dev.off()
