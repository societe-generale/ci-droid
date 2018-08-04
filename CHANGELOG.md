# Changelog - see https://keepachangelog.com for conventions

## [Unreleased]

### Added

### Changed

### Deprecated

### Removed

### Fixed
- mainClass in spring-boot-maven-plugin config was incorrect (was a copy/paste mistake from another project)

## [1.0.3] - 2018-07-13

### Fixed
- internal-api and extensions were declared as 1.0.1 in root pom.xml, but overridden as 1.0.0 in child pom -> removing the overriding.

## [1.0.2] - 2018-07-12

### Changed
- updated to internal-api and extensions 1.0.1
- now can release with Travis

## [1.0.1] - 2018-06-29

### Changed
- changed Maven group name (from cidroid to ci-droid) so that it's consistent with other CI droid modules
- refactored following recommendations from https://github.com/spring-projects/spring-boot/wiki/Building-On-Spring-Boot

## [1.0.0] - 2018-06-21 

first version !


