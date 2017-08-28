library(zoo)
library(lattice)

options(digits.secs=3)
###Lê os dados a partir do CSV passado
magnetometerBeforeCalibration <- read.zoo("/mnt/dados/Meus Documentos/Estudo/Mestrado/Repositório/trunk/Material pesquisa/Leituras de sensores/Calibração/Magnetômetro/quadcopterControllerMagnetometerBeforeCalibration.csv", index.column=0, sep=",", header=TRUE, FUN=as.numeric)
#as.xts(magnetometerBeforeCalibration)
#leituras <- as.xts(magnetometerBeforeCalibration, names=c("pressão atmosférica", "temperatura ambiente", "altitude absoluta"))

###Renomea o nome das colunas
colnames(magnetometerBeforeCalibration) [1:2] <- c("Eixo X", "Eixo Y")

plot(magnetometerBeforeCalibration$"Eixo X", magnetometerBeforeCalibration$"Eixo Y", panel.first=grid(col="lightgray", lty="dotted", lwd=par("lwd"), equilogs=TRUE), las=1, main="Intensidade do campo magnético", xlab="Eixo X (µT)", ylab="Eixo Y (µT)", pch=19) 

################################################################################
options(digits.secs=3)
###Lê os dados a partir do CSV passado
magnetometerAfterCalibration <- read.zoo("/mnt/dados/Meus Documentos/Estudo/Mestrado/Repositório/trunk/Material pesquisa/Leituras de sensores/Calibração/Magnetômetro/quadcopterControllerMagnetometerAfterCalibration.csv", index.column=0, sep=",", header=TRUE, FUN=as.numeric)
#as.xts(magnetometerAfterCalibration)
#leituras <- as.xts(magnetometerAfterCalibration, names=c("pressão atmosférica", "temperatura ambiente", "altitude absoluta"))

###Renomea o nome das colunas
colnames(magnetometerAfterCalibration) [1:2] <- c("Eixo X", "Eixo Y")

plot(magnetometerAfterCalibration$"Eixo X", magnetometerAfterCalibration$"Eixo Y", panel.first=grid(col="lightgray", lty="dotted", lwd=par("lwd"), equilogs=TRUE), las=1, main="Intensidade do campo magnético", xlab="Eixo X (µT)", ylab="Eixo Y (µT)", pch=19) 

###Imprime o gráfico de linhas
#lwd---> line width
#col---> color (red, orange, blue, blue, green and combinations with dark[nome_cor] or light[nome_cor]), to list all combinations use function colors()
#type--->"g"(grid), "l"(line), "p"(point), "r"(linear regression), "smooth"
#las--->labels are parallel (=0) or perpendicular(=2) to axis
#pch--->point character
#cex--->character extension
#lty--->line type

###Executar até antes desse comentário, depois de salvar todas as imagens executar o reset de par()

###Reseta os valores de par() apagando todas as fotos do RStudio
dev.off()
