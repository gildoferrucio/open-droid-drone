library(zoo)
library(lattice)

options(digits.secs = 3, digits = 10)
###Lê os dados a partir do CSV passado
sonarReadingsStatic <- read.zoo("/mnt/dados/Meus Documentos/Estudo/Mestrado/Repositório/trunk/Material pesquisa/Leituras de sensores/Sem filtro/Sonar/sonar_20cm_distance.csv", index.column = 1, sep = ",", header = TRUE, FUN = as.POSIXct, format='%Y-%m-%d %H:%M:%OS')
#as.xts(sonarReadingsStatic)
#leituras <- as.xts(sonarReadingsStatic, names=c("pressão atmosférica", "temperatura ambiente", "altitude absoluta"))

###Renomea o nome das colunas
colnames(sonarReadingsStatic) [1] <- c("Distância")

###Imprime o gráfico de linhas
#lwd---> line width
#col---> color (red, orange, blue, blue, green and combinations with dark[nome_cor] or light[nome_cor]), to list all combinations use function colors()
#type--->"g"(grid), "l"(line), "p"(point), "r"(linear regression), "smooth"
#las--->labels are parallel (=0) or perpendicular(=2) to axis
xyplot(sonarReadingsStatic, superpose=FALSE, type=c("g", "l"), scales=list(x=list(rot=0),y=list(rot=0)), main="Sonar estático", xlab="Tempo (hh:mm)", ylab="Distância (cm)", las=2, lwd=1)

par(mfrow=c(1,1))
#boxplot(coredata(sonarReadingsStatic$"Distância"), main="Sonar parado",  ylab="Distância (cm)", las=0)
boxplot(coredata(sonarReadingsStatic), main="Sonar estático", ylab="Distância (cm)", las=2, ycex=0.8)

#Estatísticas descritivas
summary(sonarReadingsStatic)
#Desvio padrão
sd(sonarReadingsStatic, na.rm = FALSE)

###Executar até antes desse comentário, depois de salvar todas as imagens executar o reset de par()

###Reseta os valores de par() apagando todas as fotos do RStudio
dev.off()


options(digits.secs = 3)
###Lê os dados a partir do CSV passado
sonarReadingsOscillating <- read.zoo("/mnt/dados/Meus Documentos/Estudo/Mestrado/Repositório/trunk/Material pesquisa/Leituras de sensores/Sem filtro/Sonar/sonar_20cm_60cm_distance.csv", index.column = 1, sep = ",", header = TRUE, FUN = as.POSIXct, format='%Y-%m-%d %H:%M:%OS')
#as.xts(sonarReadingsOscillating)
#leituras <- as.xts(sonarReadingsOscillating, names=c("pressão atmosférica", "temperatura ambiente", "altitude absoluta"))

###Renomea o nome das colunas
colnames(sonarReadingsOscillating) [1] <- c("Distância")

###Imprime o gráfico de linhas
#lwd---> line width
#col---> color (red, orange, blue, blue, green and combinations with dark[nome_cor] or light[nome_cor]), to list all combinations use function colors()
#type--->"g"(grid), "l"(line), "p"(point), "r"(linear regression), "smooth"
#las--->labels are parallel (=0) or perpendicular(=2) to axis
xyplot(sonarReadingsOscillating, superpose=FALSE, type=c("g", "l"), scales=list(x=list(rot=0),y=list(rot=0)), main="Sonar em movimento", xlab="Tempo (hh:mm)", ylab="Distância (cm)", las=2, lwd=1)

#par(mfrow=c(1,3))
#boxplot(coredata(sonarReadingsOscillating$"Eixo X"),  ylab="Distância (cm)", las=0)
#boxplot(coredata(sonarReadingsOscillating$"Eixo Y"), main="Sonar oscilando",  ylab="Distância (cm)", las=0)
#boxplot(coredata(sonarReadingsOscillating$"Eixo Z"), ylab="Distância (cm)", las=0)

###Executar até antes desse comentário, depois de salvar todas as imagens executar o reset de par()

###Reseta os valores de par() apagando todas as fotos do RStudio
#dev.off()
