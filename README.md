# Java Client-Server Chat System

This repository contains a **Java-based client-server chat system** with both console and GUI interfaces. Here's what's in it:

## Core Architecture

**Abstract Base Classes:**
- `AbstractServer.java` - Framework for server implementations with socket handling, client connection management, and message routing
- `AbstractClient.java` - Framework for client implementations with server connection handling and message processing

**Server Implementation:**
- `EchoServer.java` - Main chat server that extends AbstractServer with features including:
  - Room-based chat (clients can join different rooms)
  - Private messaging between users
  - "Yell" command to broadcast to all rooms
  - File transfer capabilities (upload/download/list files)
  - User management with login system
  - Default port: 5555

**Client Implementations:**
- `ChatClient.java` - Client logic handling server communication, commands, and file transfers
- `ClientConsole.java` - Console-based UI for the chat client
- `GUIConsole.java` - Swing-based graphical UI with file browser and dropdown for file management

**Supporting Classes:**
- `ConnectionToClient.java` - Represents individual client connections on the server side, manages client-specific data (userId, room)
- `Envelope.java` - Serializable message wrapper for structured commands (contains id, args, and contents fields)
- `ChatIF.java` - Interface defining display and file list methods for UI implementations

## Key Features

1. **Multi-room chat** - Users join specific rooms and only see messages from users in the same room
2. **Private messaging** - Send direct messages to specific users
3. **File transfer** - Upload files to server, list available files, download files
4. **Command system** - Commands like `#login`, `#join`, `#pm`, `#who`, `#yell`, `#ftpUpload`, `#ftpget`, `#ftplist`
5. **Dual interface** - Both command-line and GUI clients available

## Project Structure

```
/workspace/
  └── src/
      ├── AbstractClient.java
      ├── AbstractServer.java
      ├── ChatClient.java
      ├── ChatIF.java
      ├── ClientConsole.java
      ├── ConnectionToClient.java
      ├── EchoServer.java
      ├── Envelope.java
      └── GUIConsole.java
```

## Overview

The codebase appears to be an educational project demonstrating socket programming, client-server architecture, and object-oriented design patterns in Java.
