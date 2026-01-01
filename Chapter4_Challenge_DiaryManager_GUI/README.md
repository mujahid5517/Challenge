# Personal Diary Manager - JavaFX GUI Version

## 📋 Project Overview
A modern, feature-rich personal diary management application built with JavaFX. This application allows users to create, edit, browse, and search diary entries with a beautiful, intuitive interface.

## 🎯 Features

### 🏠 **Main Dashboard**
- Modern navigation pane with quick access to all features
- Welcome screen with quick actions
- Status bar with real-time updates
- Light/Dark theme toggle

### ✏️ **Rich Text Editor**
- Full-featured HTML editor for rich text formatting
- Auto-save functionality (every 2 seconds)
- Custom formatting toolbar
- Mood selection with emoji support
- Tag management system

### 📂 **Entry Browser**
- Visual list of all diary entries
- Entry previews with metadata
- Sort by date, title, or mood
- Quick actions (edit, delete, view)

### 🔍 **Advanced Search**
- Real-time search with highlighting
- Filter by mood, date, or content
- Multiple search criteria
- Search results in dedicated window

### 🎨 **Theme Support**
- Light theme (default)
- Dark theme for comfortable night use
- Instant theme switching
- Consistent styling across all components

### 💾 **File Management**
- Automatic directory creation
- Structured file naming convention
- Background file operations with progress indicators
- Error handling with user-friendly messages

## 🛠️ Technical Implementation

### **Architecture**
- **MVC Pattern**: Clean separation of concerns
- **JavaFX Framework**: Modern UI components
- **CSS Styling**: Complete theme system with light/dark modes
- **Concurrent Operations**: Background tasks for file I/O
- **FXML**: Declarative UI design

### **File Structure**
- Entries are stored in `diary-entries/` directory
- Each entry saved as `.diary` file with metadata header
- Automatic organization by date
- UTF-8 encoding for international support

### **Key Components**
1. **MainController**: Central navigation and window management
2. **EntryEditorController**: Rich text editing with auto-save
3. **DiaryManager**: File I/O operations with concurrency
4. **DiaryEntry**: Data model with metadata support
5. **CSS Themes**: Complete styling system

## 🚀 Getting Started

### **Prerequisites**
- Java JDK 11 or higher
- JavaFX SDK 17 or higher
- Maven (optional, for dependency management)

### **Running the Application**

#### **Option 1: Using Maven**
```bash
mvn clean javafx:run