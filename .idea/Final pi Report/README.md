# Coding Factory Platform Report

This folder contains the LaTeX source files for the Coding Factory Platform report.

## How to Compile the Report

### Using VS Code with LaTeX Workshop Extension

1. Open the `report.tex` file in VS Code
2. Make sure the LaTeX Workshop extension is installed and activated
3. Click the "Build LaTeX Project" button (or use the shortcut Ctrl+Alt+B)
4. View the generated PDF

### Using Command Line

If you prefer to use the command line, you can compile the report using:

```bash
pdflatex report.tex
pdflatex report.tex  # Run twice to generate table of contents correctly
```

## File Structure

- `report.tex`: Main LaTeX source file
- `README.md`: This file

## Notes

- The report uses a relative path to include the logo image. If the image doesn't appear, you may need to adjust the path in the `\includegraphics` command.
- The document is set up to use the `report` document class, which is suitable for technical reports.
- The table of contents, list of figures, and list of tables are automatically generated.

## Customizing the Report

To customize the report:

1. Edit the `report.tex` file to add your content
2. Add images to the appropriate sections using the `\includegraphics` command
3. Add tables using the `tabular` or `tabularx` environments
4. Add code listings using the `lstlisting` environment

## Required LaTeX Packages

The template uses several LaTeX packages. If you encounter errors about missing packages, you may need to install them using your LaTeX distribution's package manager.

For MiKTeX, use the MiKTeX Console to install missing packages.
For TeX Live, use `tlmgr install [package-name]` to install missing packages.
