# CAMS Image Library

Clipper Asset Management System Image Library - A Java Swing application for managing digital image assets.

## Overview

CAMS Image Library is a desktop application built with Java Swing that provides a comprehensive solution for managing, cataloging, and processing digital images. It connects to a SQL Server database to store metadata and uses ImageMagick for image processing operations.

## Features

- **Image Cataloging**: Organize images into catalogs and categories
- **Database Integration**: SQL Server backend for metadata storage
- **Image Processing**: Integration with ImageMagick for thumbnail generation and image manipulation
- **Search Functionality**: Advanced search capabilities for finding images
- **Batch Operations**: Process multiple images at once
- **User Management**: Login system with user-specific temporary directories

## Prerequisites

- Java 8 or higher
- ImageMagick installed on the system
- SQL Server database access
- Maven (for building)

## Configuration

The application uses a `cams-image.properties` file for configuration:

```properties
# Database connection
dbConnectStr=jdbc:sqlserver://your-server:1433;databasename=CAMS;encrypt=mandatory;trustServerCertificate=false;loginTimeout=30;hostNameInCertificate=*.database.windows.net;authentication=SqlPassword
dbUser=your_username
dbPass=your_password

# ImageMagick configuration
ImageAppDir=/opt/homebrew/bin/
TempDir=/tmp

# Acrobat Reader (if needed)
AcrobatReader=/Applications/Adobe Acrobat Reader DC.app/Contents/MacOS/AdobeReader
```

## Building

### Using Maven

```bash
mvn clean package
```

This will create a JAR file in the `target` directory with all dependencies included.

### Running the Application

```bash
java -jar target/cams-image-{version}.jar
```

Or if you have a pre-built JAR:

```bash
java -jar cams-image.jar
```

## Architecture

- **cams.startup**: Application startup and initialization
- **cams.database**: Database connection and operations
- **cams.imagelib**: Main UI components and image library functionality
- **cams.imaging**: ImageMagick integration for image processing
- **cams.console**: Logging and console output utilities

## Dependencies

- Microsoft JDBC Driver for SQL Server (TLS 1.2 compatible)
- Apache Commons Logging
- PDFTextStream (for PDF processing)

## Recent Updates

- Updated to use modern Microsoft SQL Server JDBC driver with TLS 1.2 support
- Fixed compatibility issues with newer Java versions
- Updated Maven build configuration
- Enhanced database connection handling for Azure SQL Database

## License

Copyright (c) 2004-2025 Clipper Digital. All rights reserved.

## Support

For support and issues, please contact the development team.