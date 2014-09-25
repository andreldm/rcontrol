#CC=C:\TDM-GCC-64\bin\gcc

# --- Windows definitions--- #
ifeq ($(OS), Windows_NT)
	OUTPUTFOLDER = "..\lib"
	RM = del /Q
	DEVNULL = NUL
	FixPath = $(subst /,\,$1)
	CopyFile = @if exist $1 copy $1 $2 /Y

# --- Linux definitions--- #
else ifeq ($(shell uname), Linux)
	OUTPUTFOLDER = "../lib"
	RM = @rm -f
	DEVNULL = /dev/null
	FixPath = $1
	MoveFile = mv $1 $2

endif

all:
	@echo Available targets:
	@echo "   linux -> linux32, linux64"
	@echo "   mingw32"
	@echo "   mingw64"

linux: linux32 linux64
linux32: librcontrol32.so
linux64: librcontrol64.so

mingw32: rcontrol32.dll
mingw64: rcontrol64.dll

librcontrol32.so:
	$(CC) -shared -fPIC -m32 linux.c -o librcontrol32.so -lX11 -lXtst -lXext -I"$(JAVA_HOME)/include" -I"$(JAVA_HOME)/include/linux"
	$(call MoveFile,$(subst \,,"$@"),$(OUTPUTFOLDER))

librcontrol64.so:
	$(CC) -shared -fPIC -m64 linux.c -o librcontrol64.so -lX11 -lXtst -lXext -I"$(JAVA_HOME)/include" -I"$(JAVA_HOME)/include/linux"
	$(call MoveFile,$(subst \,,"$@"),$(OUTPUTFOLDER))

rcontrol32.dll:
	$(CC) -shared windows.c -o rcontrol32.dll -I"$(JAVA_HOME)/include" -I"$(JAVA_HOME)/include/win32"

rcontrol64.dll:
	$(CC) -shared windows.c -o rcontrol64.dll -I"$(JAVA_HOME)/include" -I"$(JAVA_HOME)/include/win32"

clean:
	rm -f *.so *.dll

.PHONY: linux linux32 linux64 mingw32 mingw64
