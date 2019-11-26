# Title     : TODO
# Objective : TODO
# Created by: yz
# Created on: 2018/12/19
library(randomForest)
library(magrittr)
library(dplyr)
library(gbm)
rm(list = ls())
load('LiveBoost_load.RData')
new_input = read.table(quote = "", "input.txt", header = T, com = '', sep = "\t", check.names = F)
df <- new_input
Late_Fibrosis <- predict(GB_model_2, df, n.trees = 400, type = 'response')
Early_Fibrosis <- 1 - Late_Fibrosis
Cirrhosis <- predict(GB_model_3, df, n.trees = 400, type = 'response')
Fibrosis <- 1 - Cirrhosis
df_out <- data.frame(df, Fibrosis, Cirrhosis, Early_Fibrosis, Late_Fibrosis)
df_final <- df_out %>% data.frame()
df_final$results <- NA
for (i in 1 : nrow(df_final)) {
    if (df_final$Cirrhosis[i] >= 0.5) {
        df_final$results[i] <- "Cirrhosis"
    } else if (df_final$Early_Fibrosis[i] > 0.5) {
        df_final$results[i] <- "Early Fibrosis"
    } else if (df_final$Late_Fibrosis[i] >= 0.5) {
        df_final$results[i] <- "Late Fibrosis"
    }
}
# df_final$Cirrhosis <- round(df_final$Cirrhosis * 100, digits = 1)
# df_final$Late_Fibrosis <- (100 - df_final$Cirrhosis)
df_final
write.table(df_final, 'out.txt', sep = '\t', quote = F, row.names = FALSE)
