# Title     : TODO
# Objective : TODO
# Created by: yz
# Created on: 2018/12/27
library(randomForest)
library(magrittr)
library(dplyr)
library(gbm)
library(xlsx)


rm(list = ls())
load('LiveBoost_load.RData')
new_input <- read.xlsx("input.xlsx", 1, check.names = F)
df <- new_input[,c("SampleID","Age","AST","ALT","PLT")]
Late_Fibrosis <- predict(GB_model_2, df, n.trees = 400, type = 'response')
Early_Fibrosis <- 1 - Late_Fibrosis
Cirrhosis <- predict(GB_model_3, df, n.trees = 400, type = 'response')
Fibrosis <- 1 - Cirrhosis
df_out <- data.frame(df, Fibrosis, Cirrhosis, Early_Fibrosis, Late_Fibrosis)
df_final <- df_out %>% data.frame()
df_final$results <- NA
print(df_final)
for (i in 1 : nrow(df_final)) {
    if (df_final$Cirrhosis[i] >= 0.5) {
        df_final$results[i] <- "Cirrhosis"
    } else if (df_final$Early_Fibrosis[i] > 0.5) {
        df_final$results[i] <- "Early Fibrosis"
    } else if (df_final$Late_Fibrosis[i] >= 0.5) {
        df_final$results[i] <- "Late Fibrosis"
    }
}
df_final$Cirrhosis <- round(df_final$Cirrhosis * 100, digits = 1)
df_final$Fibrosis <- (100 - df_final$Cirrhosis)
df_final$Late_Fibrosis <- round(df_final$Late_Fibrosis * 100, digits = 1)
df_final$Early_Fibrosis <- (100 - df_final$Late_Fibrosis)

out<-merge(df_final, new_input,by=c("row.names","SampleID","Age","AST","ALT","PLT"),sort=F)
write.table(out, 'out.txt', sep = '\t', quote = F, row.names = FALSE)
