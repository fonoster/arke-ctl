# Routr Command-Line Tool [![Build Status][travis-badge]][travis-ci] [![Node Version][node-badge]][npm] [![NPM version][npm-badge]][npm]

The `rctl` is a command line interface tha runs commands against your `Routr` servers. This overview covers the `rctl` syntax, describes the command operations and provides common examples. For details about each command, including all the supported flags and subcommands, see the `rctl` reference documentation.

## Installation

To get the Routr Command-Line Tool run the following command:

```bash
npm install -g routr-ctl
```

This will provide you with the globally accessible `rctl` command.

## Commands

Use the following syntax to run `rctl` commands from your terminal:

```
rctl COMMAND [REF] [flags]
```

where `COMMAND`, `subcommand` `REF`, and `flags` are:

- `COMMAND`: Specifies the operation that you want to perform on one or more resources. For example: create, get, delete, locate(loc).

- `subcommand`: Specifies the resource type. Resource types are case-sensitive and you can specify the singular, plural, or abbreviated forms. For example, the following commands produce the same output:

```
  $ rctl get gateway gweef506
  $ rctl get gateways gweef506
  $ rctl get gw gweef506
```

- `REF`: Specifies the reference to the resource. References are case-sensitive. If the reference is omitted, details for all resources are displayed. For example: `$ rctl get agents`.

- `flags`: Specifies optional flags. For example, you can use the --filter to further reduce the output of `get` command.

The --filter flag uses [JsonPath](https://github.com/json-path/JsonPath) to perform the filtering. The root is always '$'
so all you need to add is the path to the property and the filter operators. For example:

```
# This will return all the DIDs in Gateway 'gweef506'
./rctl get dids --filter "@.metadata.gwRef=='gweef506'"    
```

If you need help, just run `rctl --help` from the terminal window.

```bash
$ ./rctl -h
usage: rctl [-h] COMMAND ...

rctl controls the Routr server

named arguments:
  -h, --help             show this help message and exit

Basic Commands:
  COMMAND
    get                  display a list of resources
    create (crea)        creates new resource(s)
    apply                apply changes over existing resource(s)
    delete (del)         delete an existing resource(s)
    locate (loc)         locate sip device(s)
    registry (reg)       shows gateways registrations
    system (sys)         display a list of resources
    proxy                run a proxy to the server (alpha)    
    login                sets connection info

More information at https://routr.io
```

> **Important**: Some commands (ie.: create, delete) are not available in the default implementation of the `resources` modules. Only persistent implementations will allow such command.

### Examples: Common operations

Use the following set of examples to help you familiarize yourself with running the commonly used `rctl` operations:

`rctl locate` or `rctl loc` - Locate a sip device registered on the Routr server

```
// Locate all Sip Devices registered against a Routr server
$ rctl loc
```

`rctl registry` or `rctl reg` - Shows Gateways current registration

```
// Shows the registry
$ rctl reg
```

`rctl get` - List one or more resources.

```
// List all dids
$ rctl get dids

// List all dids that belong to gateway reference gweef506
$ rctl get dids --filter "@.metadata.ref=='gweef506'"

// List did by reference
$ rctl get dids dd50baa4

// List all agents
$ rctl get agents
```

`rctl create` - create a new resource.

```
// Create a new gateway(s) using a .yaml or .yml file
$ rctl create -f new-gateway.yaml
```

`rctl apply` - update an existing resource(s)

```
// Update an existing resource(s) .yaml or .yml.
$ rctl apply -f new-gateway.yaml
```

`rctl delete` - delete a resource.

```
// Delete all did for gateway reference gweef506
$ rctl delete dids --filter "@.metadata.gwRef=='gweef506'"

// Delete a single agent (using delete alias)
$ rctl del agent ag3f77f6
```

## Cheat Sheet

> Create, delete, and update are only available in some implementations of the `resources` module.

### Request and store token

```
# Request authentication for subsecuent commands
$ rctl login https://127.0.0.1/api/{apiVersion} -u admin -p changeit
```

### Showing the Registry

```
# Shows all the Gateways that are currently available
$ rctl registry                                       # Shows only current registrations. You may use `reg` for short
```

### Locating Sip Devices

```
# Find all sip devices available at the location service
$ rctl locate                                         # This list will not include did-ingress-routes or domain-egress-routes
```

### Creating Resources

```
# Create new peers and agents
$ rctl create -f asterisk.yaml                        # Create Peer in file asterisk.yaml
$ rctl create -f agents-list.yaml                     # Create Agents in file agents-list.yaml
```

### Finding Resources

```
# Get DIDs
$ rctl get dids                                          # List all available DIDs
$ rctl get did                                           # List all available DIDs
$ rctl get did --filter "@.metadata.ref=='dd50baa4'"     # Shows DID with reference 'DID0001'
$ rctl get did --filter "@.metadata.gwRef=='gweef506'"   # Shows DIDs with Gateway reference 'GW1232'

# Get agents
$ rctl get agents                                        # List all Agents
```

### Deleting Resources

```
# Delete command by refernce or filter
$ rctl delete agent ag3f77f6                             # Delete Agent by reference
$ rctl del dids --filter '@.metadata.gwRef=gweef506'     # Delete DIDs using a filter
```

### Updating Resources

```
$ rctl -- apply -f asterisk.yaml                         # Create Peer in file asterisk.yaml
$ rctl -- apply -f agents-list.yaml                      # Create Agents in file agents-list.yaml
```

## Bugs and Feedback

For bugs, questions, and discussions please use the [Github Issues](https://github.com/fonoster/routr-ctl/issues)

## Contributing

For contributing, please see the following links:

 - [Contribution Documents](https://github.com/fonoster/routr/blob/master/CONTRIBUTING.md)
 - [Contributors](https://github.com/fonoster/routr-ctl/contributors)

## Authors
 - [Pedro Sanders](https://github.com/psanders)

## LICENSE
Copyright (C) 2018 by [Fonoster Inc](https://fonoster.com). MIT License (see [LICENSE](https://github.com/fonoster/routr/blob/master/LICENSE) for details).

[travis-ci]: https://travis-ci.org/fonoster/routr-ctl
[npm]: https://www.npmjs.com/package/routr-ctl

[travis-badge]: https://img.shields.io/travis/fonoster/routr-ctl/master.svg
[node-badge]: https://img.shields.io/node/v/routr-ctl.svg
[npm-badge]: https://img.shields.io/npm/v/routr-ctl.svg
