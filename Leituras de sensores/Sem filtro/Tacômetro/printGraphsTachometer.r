library(zoo)
library(lattice)

options(digits.secs = 3)
###Lê os dados a partir do CSV passado
photoreflectorStatic <- read.zoo("/mnt/dados/Meus Documentos/Estudo/Mestrado/Repositório/trunk/Material pesquisa/Leituras de sensores/Sem filtro/Tacômetro/photoreflector_static_throttle25percent_uncalibratedESC.csv", index.column = 1, sep = ",", header = TRUE, FUN = as.POSIXct, format='%Y-%m-%d %H:%M:%OS')
#as.xts(photoreflectorStatic)
#leituras <- as.xts(photoreflectorStatic, names=c("pressão atmosférica", "temperatura ambiente", "altitude absoluta"))

###Renomea o nome das colunas
colnames(photoreflectorStatic) [1] <- c("Velocidade")

###Imprime o gráfico de linhas
#lwd---> line width
#col---> color (red, orange, blue, blue, green and combinations with dark[nome_cor] or light[nome_cor]), to list all combinations use function colors()
#type--->"g"(grid), "l"(line), "p"(point), "r"(linear regression), "smooth"
#las--->labels are parallel (=0) or perpendicular(=2) to axis
xyplot(photoreflectorStatic, superpose=FALSE, type=c("g", "l"), main="Tacômetro em rotação constante", xlab="Tempo (hh:mm)", ylab="Velocidade (RPM)", las=2, lwd=1)

par(mfrow=c(1,1))
#boxplot(coredata(photoreflectorStatic$"Distância"), main="Fotorrefletor constante",  ylab="Distância (cm)", las=0)
boxplot(coredata(photoreflectorStatic), main="Tacômetro em rotação constante", ylab="Velocidade (RPM)", las=2)

################################################################################
options(digits.secs = 3, digits = 10)
###Lê os dados a partir do CSV passado
tachometerStatic <- read.zoo("/mnt/dados/Meus Documentos/Estudo/Mestrado/Repositório/trunk/Material pesquisa/Leituras de sensores/Sem filtro/Tacômetro/quadcopterControllerLogDeviceTests_tachometer_static.csv", index.column = 1, sep = ",", header = TRUE, FUN = as.POSIXct, format='%H:%M:%OS')
#as.xts(tachometerStatic)
#leituras <- as.xts(tachometerStatic, names=c("pressão atmosférica", "temperatura ambiente", "altitude absoluta"))

###Renomea o nome das colunas
colnames(tachometerStatic) [1] <- c("Velocidade")

###Imprime o gráfico de linhas
#lwd---> line width
#col---> color (red, orange, blue, blue, green and combinations with dark[nome_cor] or light[nome_cor]), to list all combinations use function colors()
#type--->"g"(grid), "l"(line), "p"(point), "r"(linear regression), "smooth"
#las--->labels are parallel (=0) or perpendicular(=2) to axis
xyplot(tachometerStatic, superpose=FALSE, type=c("g", "l"), scales=list(x=list(rot=0),y=list(rot=0)), main="Tacômetro em rotação constante", xlab="Tempo (hh:mm)", ylab="Velocidade (RPM)", las=2, lwd=1)

par(mfrow=c(1,1))
#boxplot(coredata(tachometerStatic$"Distância"), main="Fotorrefletor constante",  ylab="Distância (cm)", las=0)
boxplot(coredata(tachometerStatic), main="Tacômetro em rotação constante", ylab="Velocidade (RPM)", las=2)

#Estatísticas descritivas
summary(tachometerStatic)
#Desvio padrão
sd(tachometerStatic, na.rm = FALSE)

###Executar até antes desse comentário, depois de salvar todas as imagens executar o reset de par()

###Reseta os valores de par() apagando todas as fotos do RStudio
dev.off()

################################################################################
options(digits.secs = 3)
###Lê os dados a partir do CSV passado
tachometerStatic <- read.zoo("/mnt/dados/Meus Documentos/Estudo/Mestrado/Repositório/trunk/Material pesquisa/Leituras de sensores/Sem filtro/Tacômetro/quadcopterControllerLogDeviceTests_tachometer_static.csv", index.column = 1, sep = ",", header = TRUE, FUN = as.POSIXct, format='%H:%M:%OS')
#as.xts(tachometerStatic)
#leituras <- as.xts(tachometerStatic, names=c("pressão atmosférica", "temperatura ambiente", "altitude absoluta"))

###Renomea o nome das colunas
colnames(tachometerStatic) [1] <- c("Revolutions")

###Imprime o gráfico de linhas
#lwd---> line width
#col---> color (red, orange, blue, blue, green and combinations with dark[nome_cor] or light[nome_cor]), to list all combinations use function colors()
#type--->"g"(grid), "l"(line), "p"(point), "r"(linear regression), "smooth"
#las--->labels are parallel (=0) or perpendicular(=2) to axis
xyplot(tachometerStatic, superpose=FALSE, type=c("g", "l"), scales=list(x=list(rot=0),y=list(rot=0)), main="Tachometer - Rotors at constant speed", xlab="Time (hh:mm)", ylab="Revolutions (RPM)", las=2, lwd=1)

################################################################################
options(digits.secs = 3)
###Lê os dados a partir do CSV passado
tachometerOscillating <- read.zoo("/mnt/dados/Meus Documentos/Estudo/Mestrado/Repositório/trunk/Material pesquisa/Leituras de sensores/Sem filtro/Tacômetro/quadcopterControllerLogDeviceTests_tachometer4_oscillating.csv", index.column = 1, sep = ",", header = TRUE, FUN = as.POSIXct, format='%H:%M:%OS')
#as.xts(tachometerOscillating)
#leituras <- as.xts(tachometerOscillating, names=c("pressão atmosférica", "temperatura ambiente", "altitude absoluta"))

###Renomea o nome das colunas
colnames(tachometerOscillating) [1] <- c("Velocidade")

###Imprime o gráfico de linhas
#lwd---> line width
#col---> color (red, orange, blue, blue, green and combinations with dark[nome_cor] or light[nome_cor]), to list all combinations use function colors()
#type--->"g"(grid), "l"(line), "p"(point), "r"(linear regression), "smooth"
#las--->labels are parallel (=0) or perpendicular(=2) to axis
xyplot(tachometerOscillating, superpose=FALSE, type=c("g", "l"), scales=list(x=list(rot=0),y=list(rot=0)), main="Tacômetro em rotação variável", xlab="Tempo (hh:mm)", ylab="Velocidade (RPM)", las=2, lwd=1)

################################################################################
options(digits.secs = 3)
###Lê os dados a partir do CSV passado
tachometerOscillating <- read.zoo("/mnt/dados/Meus Documentos/Estudo/Mestrado/Repositório/trunk/Material pesquisa/Leituras de sensores/Sem filtro/Tacômetro/quadcopterControllerLogDeviceTests_tachometer4_oscillating.csv", index.column = 1, sep = ",", header = TRUE, FUN = as.POSIXct, format='%H:%M:%OS')
#as.xts(tachometerOscillating)
#leituras <- as.xts(tachometerOscillating, names=c("pressão atmosférica", "temperatura ambiente", "altitude absoluta"))

###Renomea o nome das colunas
colnames(tachometerOscillating) [1] <- c("Revolutions")

###Imprime o gráfico de linhas
#lwd---> line width
#col---> color (red, orange, blue, blue, green and combinations with dark[nome_cor] or light[nome_cor]), to list all combinations use function colors()
#type--->"g"(grid), "l"(line), "p"(point), "r"(linear regression), "smooth"
#las--->labels are parallel (=0) or perpendicular(=2) to axis
xyplot(tachometerOscillating, superpose=FALSE, type=c("g", "l"), scales=list(x=list(rot=0),y=list(rot=0)), main="Tachometer - Rotors at variable rotation", xlab="Time (hh:mm)", ylab="Revolutions (RPM)", las=2, lwd=1)


#options(digits.secs = 3)
###Lê os dados a partir do CSV passado
#sonarReadingsOscillating <- read.zoo("/mnt/dados/Meus Documentos/Estudo/Mestrado/Material pesquisa/Leituras de sensores/Sem filtro/Fotorrefletor/sonar_20cm_60cm_distance.csv", index.column = 1, sep = ",", header = TRUE, FUN = as.POSIXct)
#as.xts(sonarReadingsOscillating)
#leituras <- as.xts(sonarReadingsOscillating, names=c("pressão atmosférica", "temperatura ambiente", "altitude absoluta"))

###Renomea o nome das colunas
#colnames(sonarReadingsOscillating) [1] <- c("Distância")

###Imprime o gráfico de linhas
#lwd---> line width
#col---> color (red, orange, blue, blue, green and combinations with dark[nome_cor] or light[nome_cor]), to list all combinations use function colors()
#type--->"g"(grid), "l"(line), "p"(point), "r"(linear regression), "smooth"
#las--->labels are parallel (=0) or perpendicular(=2) to axis
#xyplot(sonarReadingsOscillating, superpose=FALSE, type=c("g", "l"), main="Sonar oscilando", xlab="Tempo (hh:mm)", ylab="Distância (cm)", las=2, lwd=1)

#par(mfrow=c(1,3))
#boxplot(coredata(sonarReadingsOscillating$"Eixo X"),  ylab="Distância (cm)", las=0)
#boxplot(coredata(sonarReadingsOscillating$"Eixo Y"), main="Sonar oscilando",  ylab="Distância (cm)", las=0)
#boxplot(coredata(sonarReadingsOscillating$"Eixo Z"), ylab="Distância (cm)", las=0)

###Executar até antes desse comentário, depois de salvar todas as imagens executar o reset de par()

###Reseta os valores de par() apagando todas as fotos do RStudio
#dev.off()
