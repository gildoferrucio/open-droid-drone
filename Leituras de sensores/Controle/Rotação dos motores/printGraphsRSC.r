library(zoo)
library(lattice)

options(digits.secs = 3)
###Lê os dados a partir do CSV passado
rscPidKp005 <- read.zoo("/mnt/dados/Meus Documentos/Estudo/Mestrado/Repositório/trunk/Material pesquisa/Leituras de sensores/Controle/Rotação dos motores/rsc_P0,05_I0_D0_BV0,06.csv", index.column = 1, sep = ",", header = TRUE, FUN = as.POSIXct, format='%H:%M:%OS')
#as.xts(rscPidKp005)
#leituras <- as.xts(rscPidKp005, names=c("pressão atmosférica", "temperatura ambiente", "altitude absoluta"))

###Renomea o nome das colunas
colnames(rscPidKp005) [1:2] <- c(expression("Variável de referência", "Variável de processo"))

###Imprime o gráfico de linhas
#lwd---> line width
#col---> color (red, orange, blue, blue, green and combinations with dark[nome_cor] or light[nome_cor]), to list all combinations use function colors()
#type--->"g"(grid), "l"(line), "p"(point), "r"(linear regression), "smooth"
#las--->labels are parallel (=0) or perpendicular(=2) to axis
#pch--->point character
#cex--->character extension
#lty--->line type
xyplot(rscPidKp005, superpose=TRUE, type=c("g", "l"), scales=list(x=list(rot=0),y=list(rot=0)), pch=c(1:14), cex=1, lty=c(1:4), main="RSC - PID (Kp=0,05)", xlab="Tempo (segs)", ylab="Revoluções [RPM]", las=2, lwd=1)

#boxplot(coredata(rscPidKp005$"Barômetro [m]"), main="Altitude - Barômetro estático", ylab="Barômetro (m)", las=0)

################################################################
options(digits.secs = 3)
###Lê os dados a partir do CSV passado
rscPidKp003 <- read.zoo("/mnt/dados/Meus Documentos/Estudo/Mestrado/Repositório/trunk/Material pesquisa/Leituras de sensores/Controle/Rotação dos motores/rsc_P0,03_I0_D0_BV0,155.csv", index.column = 1, sep = ",", header = TRUE, FUN = as.POSIXct, format='%H:%M:%OS')
#as.xts(rscPidKp003)
#leituras <- as.xts(rscPidKp003, names=c("pressão atmosférica", "temperatura ambiente", "altitude absoluta"))

###Renomea o nome das colunas
colnames(rscPidKp003) [1:2] <- c(expression("Variável de referência", "Variável de processo"))

###Imprime o gráfico de linhas
#lwd---> line width
#col---> color (red, orange, blue, blue, green and combinations with dark[nome_cor] or light[nome_cor]), to list all combinations use function colors()
#type--->"g"(grid), "l"(line), "p"(point), "r"(linear regression), "smooth"
#las--->labels are parallel (=0) or perpendicular(=2) to axis
#pch--->point character
#cex--->character extension
#lty--->line type
xyplot(rscPidKp003, superpose=TRUE, type=c("g", "l"), scales=list(x=list(rot=0),y=list(rot=0)), pch=c(1:14), cex=1, lty=c(1:4), main="RSC - PID (Kp=0,03)", xlab="Tempo (segs)", ylab="Revoluções [RPM]", las=2, lwd=1)

#boxplot(coredata(rscPidKp003$"Barômetro [m]"), main="Altitude - Barômetro estático", ylab="Barômetro (m)", las=0)

################################################################
options(digits.secs = 3)
###Lê os dados a partir do CSV passado
rscPidKp001 <- read.zoo("/mnt/dados/Meus Documentos/Estudo/Mestrado/Repositório/trunk/Material pesquisa/Leituras de sensores/Controle/Rotação dos motores/rsc_P0,01_I0_D0_BV0,085.csv", index.column = 1, sep = ",", header = TRUE, FUN = as.POSIXct, format='%H:%M:%OS')
#as.xts(rscPidKp001)
#leituras <- as.xts(rscPidKp001, names=c("pressão atmosférica", "temperatura ambiente", "altitude absoluta"))

###Renomea o nome das colunas
colnames(rscPidKp001) [1:2] <- c(expression("Variável de referência", "Variável de processo"))

###Imprime o gráfico de linhas
#lwd---> line width
#col---> color (red, orange, blue, blue, green and combinations with dark[nome_cor] or light[nome_cor]), to list all combinations use function colors()
#type--->"g"(grid), "l"(line), "p"(point), "r"(linear regression), "smooth"
#las--->labels are parallel (=0) or perpendicular(=2) to axis
#pch--->point character
#cex--->character extension
#lty--->line type
xyplot(rscPidKp001, superpose=TRUE, type=c("g", "l"), scales=list(x=list(rot=0),y=list(rot=0)), pch=c(1:14), cex=1, lty=c(1), main="RSC - PID (Kp=0,01)", xlab="Tempo (segs)", ylab="Revoluções [RPM]", las=2, lwd=1)

#boxplot(coredata(rscPidKp001$"Barômetro [m]"), main="Altitude - Barômetro estático", ylab="Barômetro (m)", las=0)

################################################################
options(digits.secs = 3)
###Lê os dados a partir do CSV passado
rscPidKp002 <- read.zoo("/mnt/dados/Meus Documentos/Estudo/Mestrado/Repositório/trunk/Material pesquisa/Leituras de sensores/Controle/Rotação dos motores/rsc_P0,02_I0_D0_BV0,085.csv", index.column = 1, sep = ",", header = TRUE, FUN = as.POSIXct, format='%H:%M:%OS')
#as.xts(rscPidKp002)
#leituras <- as.xts(rscPidKp002, names=c("pressão atmosférica", "temperatura ambiente", "altitude absoluta"))

###Renomea o nome das colunas
colnames(rscPidKp002) [1:2] <- c(expression("Variável de referência", "Variável de processo"))

###Imprime o gráfico de linhas
#lwd---> line width
#col---> color (red, orange, blue, blue, green and combinations with dark[nome_cor] or light[nome_cor]), to list all combinations use function colors()
#type--->"g"(grid), "l"(line), "p"(point), "r"(linear regression), "smooth"
#las--->labels are parallel (=0) or perpendicular(=2) to axis
#pch--->point character
#cex--->character extension
#lty--->line type
xyplot(rscPidKp002, superpose=TRUE, type=c("g", "l"), scales=list(x=list(rot=0),y=list(rot=0)), pch=c(1:14), cex=1, lty=c(1), main="RSC - PID (Kp=0,02)", xlab="Tempo (segs)", ylab="Revoluções [RPM]", las=2, lwd=1)

################################################################
options(digits.secs = 3)
###Lê os dados a partir do CSV passado
rscPidKp0025 <- read.zoo("/mnt/dados/Meus Documentos/Estudo/Mestrado/Repositório/trunk/Material pesquisa/Leituras de sensores/Controle/Rotação dos motores/rsc_P0,025_I0_D0_BV0,1.csv", index.column = 1, sep = ",", header = TRUE, FUN = as.POSIXct, format='%H:%M:%OS')
#as.xts(rscPidKp0025)
#leituras <- as.xts(rscPidKp0025, names=c("pressão atmosférica", "temperatura ambiente", "altitude absoluta"))

###Renomea o nome das colunas
colnames(rscPidKp0025) [1:2] <- c(expression("Variável de referência", "Variável de processo"))

###Imprime o gráfico de linhas
#lwd---> line width
#col---> color (red, orange, blue, blue, green and combinations with dark[nome_cor] or light[nome_cor]), to list all combinations use function colors()
#type--->"g"(grid), "l"(line), "p"(point), "r"(linear regression), "smooth"
#las--->labels are parallel (=0) or perpendicular(=2) to axis
#pch--->point character
#cex--->character extension
#lty--->line type
xyplot(rscPidKp0025, superpose=TRUE, type=c("g", "l"), scales=list(x=list(rot=0),y=list(rot=0)), pch=c(1:14), cex=1, lty=c(1), main="RSC - PID (Kp=0,025)", xlab="Tempo (segs)", ylab="Revoluções [RPM]", las=2, lwd=1)

################################################################
options(digits.secs = 3)
###Lê os dados a partir do CSV passado
rscPidKp002BV01 <- read.zoo("/mnt/dados/Meus Documentos/Estudo/Mestrado/Repositório/trunk/Material pesquisa/Leituras de sensores/Controle/Rotação dos motores/rsc_P0,02_I0_D0_BV0,1.csv", index.column = 1, sep = ",", header = TRUE, FUN = as.POSIXct, format='%H:%M:%OS')
#as.xts(rscPidKp002BV01)
#leituras <- as.xts(rscPidKp002BV01, names=c("pressão atmosférica", "temperatura ambiente", "altitude absoluta"))

###Renomea o nome das colunas
colnames(rscPidKp002BV01) [1:2] <- c(expression("Variável de referência", "Variável de processo"))

###Imprime o gráfico de linhas
#lwd---> line width
#col---> color (red, orange, blue, blue, green and combinations with dark[nome_cor] or light[nome_cor]), to list all combinations use function colors()
#type--->"g"(grid), "l"(line), "p"(point), "r"(linear regression), "smooth"
#las--->labels are parallel (=0) or perpendicular(=2) to axis
#pch--->point character
#cex--->character extension
#lty--->line type
xyplot(rscPidKp002BV01, superpose=TRUE, type=c("g", "l"), scales=list(x=list(rot=0),y=list(rot=0)), pch=c(1:14), cex=1, lty=c(1:4), main="Controle de rotação dos motores (Kp=0,02)", xlab="Tempo (segs)", ylab="Revoluções [RPM]", las=2, lwd=1)

################################################################
options(digits.secs = 3)
###Lê os dados a partir do CSV passado
rscPidKp002Ki001BV01 <- read.zoo("/mnt/dados/Meus Documentos/Estudo/Mestrado/Repositório/trunk/Material pesquisa/Leituras de sensores/Controle/Rotação dos motores/rsc_P0,02_I0,01_D0_BV0,1.csv", index.column = 1, sep = ",", header = TRUE, FUN = as.POSIXct, format='%H:%M:%OS')
#as.xts(rscPidKp002Ki001BV01)
#leituras <- as.xts(rscPidKp002Ki001BV01, names=c("pressão atmosférica", "temperatura ambiente", "altitude absoluta"))

###Renomea o nome das colunas
colnames(rscPidKp002Ki001BV01) [1:2] <- c(expression("Variável de referência", "Variável de processo"))

###Imprime o gráfico de linhas
#lwd---> line width
#col---> color (red, orange, blue, blue, green and combinations with dark[nome_cor] or light[nome_cor]), to list all combinations use function colors()
#type--->"g"(grid), "l"(line), "p"(point), "r"(linear regression), "smooth"
#las--->labels are parallel (=0) or perpendicular(=2) to axis
#pch--->point character
#cex--->character extension
#lty--->line type
xyplot(rscPidKp002Ki001BV01, superpose=TRUE, type=c("g", "l"), scales=list(x=list(rot=0),y=list(rot=0)), pch=c(1:14), cex=1, lty=c(1:4), main="Controle de rotação dos motores (Kp=0,02, Ki=0,01)", xlab="Tempo (segs)", ylab="Revoluções [RPM]", las=2, lwd=1)

###Executar até antes desse comentário, depois de salvar todas as imagens executar o reset de par()

###Reseta os valores de par() apagando todas as fotos do RStudio
dev.off()
