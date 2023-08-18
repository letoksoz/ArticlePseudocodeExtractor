#!/bin/bash

# Provide the directory path
directory="/Users/Levent/Desktop/pseudo_code_data/latex_and_pdf"

# Check if the directory exists
if [ -d "$directory" ]; then
  # List all files ending with .tex
  files=$(find "$directory" -type f -name "*.tex")
  # Iterate over each file in the directory
  for file in $files; do
    # Check if the file is a regular file (not a directory)
    if [ -f "$file" ]; then
      # Print the filename
      echo "$file"
      pdflatex -interaction=nonstopmode -output-directory $directory $file
    fi
  done
else
  echo "Directory not found."
fi
