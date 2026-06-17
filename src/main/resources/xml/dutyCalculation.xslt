<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exsl="http://exslt.org/common"
                exclude-result-prefixes="exsl">

    <xsl:output method="xml" indent="yes" encoding="UTF-8"/>

    <!-- Основной шаблон: двухпроходная обработка -->
    <xsl:template match="/">
        <!-- Первый проход: вставляем calculatedDuty в каждый item -->
        <xsl:variable name="withDuty">
            <xsl:apply-templates mode="addDuty"/>
        </xsl:variable>

        <!-- Второй проход: дополняем результат тегом totalDuty -->
        <xsl:apply-templates select="exsl:node-set($withDuty)" mode="addTotal"/>
    </xsl:template>

    <!-- Режим addDuty: копирование всего с добавлением calculatedDuty -->
    <xsl:template match="*|@*|text()" mode="addDuty">
        <xsl:copy>
            <xsl:apply-templates select="*|@*|text()" mode="addDuty"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="item" mode="addDuty">
        <xsl:copy>
            <xsl:apply-templates select="@*|*|text()" mode="addDuty"/>
            <calculatedDuty>
                <xsl:value-of select="translate(customsValue, ',', '') * dutyRate div 100"/>
            </calculatedDuty>
        </xsl:copy>
    </xsl:template>

    <!-- Режим addTotal: копирование итогового дерева и вставка totalDuty -->
    <xsl:template match="*|@*|text()" mode="addTotal">
        <xsl:copy>
            <xsl:apply-templates select="*|@*|text()" mode="addTotal"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="goods" mode="addTotal">
        <xsl:copy>
            <xsl:apply-templates select="@*|*|text()" mode="addTotal"/>
            <totalDuty>
                <xsl:value-of select="format-number(sum(item/calculatedDuty), '0.00')"/>
            </totalDuty>
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>