Author: Neil Hegde (c) 2012,
unless otherwise noted. Some files provided by course. 

# Visualizing FEC Data

Aggregates federal election contributions by state and creates an interactive visualization to better explore the data. Unfortunately this requires a bit of setup to get it working at the moment, a database needs to be downloaded. Made using D3.js, python flask, and PostgreSQL. 

For PostgreSQL queries to generate a dataset similar to the one used in the visualization, run the "test.sh" file in the "PostgreSQL Queries on FEC Database" folder

The following instructions will walk you through how to set up the appropriate dev environment to run the visualization (install_vm_requirements.sh has a list of required packages/languages you'll need).

## Setup

### Vagrant & Ubuntu setup
If you're in a VM or Ubuntu, you'll need to run a command that will install external dependencies: (skip step if not in vagrant/ubuntu)

    $ cd $BASE_DIR
    $ ./install_vm_requirements.sh


To get started, you'll need to bootstrap the database.

    $ cd $BASE_DIR/bootstrap
    $ ./import-fec.sh

This will populate a database that is configured with credentials accessible by the Flask web service.

There are a few python packages needed before we can begin.

You can install the requirements with [`pip`](https://pip.pypa.io/en/latest/):

    $ cd $BASE_DIR
    $ pip install -r requirements.txt  # may need to be sudo if not in a virtualenv

Then you should be ready to go! To run the web server, run`./serve.py` in a shell which will deploy the application on localhost port 5000.

#### Python files
- `myfecviz/views/fec.py`
- `myfecviz/services/fec.py`

#### Template file
- `myfecviz/templates/index.html`

#### JavaScript files
- `myfecviz/static/js/app.js`
- `myfecviz/static/js/vis/geocashmap.js`
- `myfecviz/static/js/vis/transactionhistogram.js`
