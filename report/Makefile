DOCUMENT_NAME = report
all: $(DOCUMENT_NAME).pdf 

PDFTEXFLAGS = "--shell-escape"

$(DOCUMENT_NAME).pdf: $(DOCUMENT_NAME).tex
	pdflatex $(PDFTEXFLAGS) $(DOCUMENT_NAME).tex
	pdflatex $(PDFTEXFLAGS) $(DOCUMENT_NAME).tex
	pdflatex $(PDFTEXFLAGS) $(DOCUMENT_NAME).tex


open: $(DOCUMENT_NAME).pdf
	google-chrome $(DOCUMENT_NAME).pdf &

clean:
	@rm -f *.log *.aux $(DOCUMENT_NAME).pdf *.toc

