# Inventory Management System

## Table of Contents
1. [Description](#description)
2. [Getting Started](#getting-started)
    * [Technologies and Libraries](#technologies-and-libraries)
    * [How to Run](#how-to-run)
3. [Usage](#usage)
4. [Acknowledgements](#acknowledgements)

## Description
The Inventory Management System implemented using Java Swing, JDBC API, and SQLite.
The goal of this application is for users to track and modify inventory items and sales.

## Getting Started
### Technologies
* Java 1.7
* Java Swing
* JDBC API
* SQLite 
* Maven
* JFreeChart
* IDE: NetBeans

### How to Run
In the terminal, paste: git clone https://github.com/nrudeys/Inventory-Management-System.git or 
download the repository as a zip file, extract, and locate/open IMSys.exe. 
<br> NOTE: Do not remove the IMSys.exe from the folder as it is dependent on IMS.db 

## Usage
### Tables
In the inventory and sales tables, users can SELECT, INSERT, UPDATE, and DELETE an entry. 
These features are access through the Add and Edit buttons located in the upper right of the application. 
Additionally, both tables can be sorted by columns either in ascending or descending order. 
This is done by clicking on the column name of either table. <br>
<br> 
Note: the inventory table profit and ROI column values are generated when inventory items are added into the sales 
table.<br>
<br>
See examples folder to see inventory/sales table usage<br>

### Analysis
This feature uses the data presented in the tables to create category, sales, line/ROI, platform, and fees graphs
for a given year (selling date year). The also analysis provides information of total available items and
total profit generated. Users can then choose to print the results. The purpose of this feature is to display the
data given in a meaningful manner.<br>
<br>
See examples folder to see analysis usage<br>

 
### Settings
Setting options are: Color Settings, Clear Tables, Print Inventory, Print Sales. <br>
<br>
Color Settings: Users can customize the application by applying a color scheme of their choosing. The chosen color is then
stored for the next session.<br>
Clear Tables: Clears tables of all data<br>
Print Inventory/Sales: Opens dialog for printing<br>
<br>
See examples folder to see settings usage<br>


## Acknowledgements
* [Bandicam](https://www.bandicam.com/)
* [Flaticon] - Inventory icons created by Nhor Phai](https://www.flaticon.com/free-icons/inventory)
* [Launch4j] (http://launch4j.sourceforge.net/)
* [MVNRepository](https://mvnrepository.com/)
