@echo off

docbook2fo.bat %1 _temp_.fo && fo2pdf.bat _temp_.fo %2 && del _temp_.fo