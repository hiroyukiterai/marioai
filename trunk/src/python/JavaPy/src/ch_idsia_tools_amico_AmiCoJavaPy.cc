/* 
 * File:   ch_idsia_tools_amico_JavaPy.cc
 * Author: nikolay
 *
 * Created on October 24, 2010, 3:49 PM
 */

#include <Python.h>
#include <jni.h>
#include "ch_idsia_tools_amico_AmiCoJavaPy.h"
#include "arrayutils.h"
#include <iostream>

static std::string AMICO_WARNING = "[AmiCo Warning] : ";
static std::string AMICO_ERROR = "[AmiCo Warning] : ";
static std::string AMICO_INFO = "[AmiCo Info] : ";
static std::string AMICO_EXCEPTION = "[AmiCo Exception] : ";

static int ERROR_PYTHON_IS_NOT_INITIALIZED = -1;
static int SUCCESS = 0;

PyObject* mainModule;

const char* agentName;

JNIEXPORT jint JNICALL
Java_ch_idsia_tools_amico_JavaPy_initModule(JNIEnv *env,
                                            jobject obj,
                                            jstring moduleNameJ)
{
    std::cout << AMICO_INFO << "Initializing python environment" << std::endl;
    Py_Initialize();
    if (Py_IsInitialized())
        std::cout << AMICO_INFO << "Python environment initialized successfuly" << std::endl;
    else
    {
        std::cerr << AMICO_EXCEPTION << "Python environment initialization failed!" << std::endl;
        return ERROR_PYTHON_IS_NOT_INITIALIZED;
        //throw (AMICO_EXCEPTION + "Python environment initialization failed!");
    }

    const char* moduleName = (env)->GetStringUTFChars(moduleNameJ, NULL);
    mainModule = PyImport_ImportModule(moduleName);
    if (mainModule != 0)
        std::cout << AMICO_INFO << "Main module has been loaded successfuly" << std::endl;
    else
    {
        std::cout << AMICO_ERROR << "Main module had not been loaded successfuly. Details:" << std::endl;
        PyErr_Print();
        return ERROR_PYTHON_IS_NOT_INITIALIZED;
    }
    return SUCCESS;
}

JNIEXPORT jintArray JNICALL
Java_ch_idsia_tools_amico_JavaPy_integrateObservation(JNIEnv *env,
                                                      jobject obj,
                                                      jintArray squashedObservation,
                                                      jintArray squashedEnemies,
                                                      jfloatArray marioPos,
                                                      jfloatArray enemiesPos,
                                                      jintArray marioState)
{
    PyObject* sqObs = convertJavaArrayToPythonArray<jintArray, jint>(env, squashedObservation, 'I');
    PyObject* sqEn = convertJavaArrayToPythonArray<jintArray, jint>(env, squashedEnemies, 'I');
    PyObject* mPos = convertJavaArrayToPythonArray<jfloatArray, jfloat>(env, marioPos, 'F');
    PyObject* enPos = convertJavaArrayToPythonArray<jfloatArray, jfloat>(env, enemiesPos, 'F');
    PyObject* mState = convertJavaArrayToPythonArray<jintArray, jint>(env, marioState, 'I');
    PyObject* res = PyObject_CallMethod(mainModule, "integrateObservation", "((items)(items)(items)(items)(items))", sqObs, sqEn, mPos, enPos, mState);
    PyObject* actions = PyObject_CallMethod(mainModule, "getAction", "()");
    if (actions == NULL)
        std::cout << AMICO_ERROR << "Actions is NULL!" << std::endl;
    
    return actions;
}

JNIEXPORT jstring JNICALL
Java_ch_idsia_tools_amico_AmiCoJavaPy_getName(JNIEnv *, jobject)
{
    return NULL;
}

JNIEXPORT jintArray JNICALL
Java_ch_idsia_tools_amico_AmiCoJavaPy_getAction(JNIEnv *, jobject)
{
    return NULL;
}

JNIEXPORT void JNICALL
Java_ch_idsia_tools_amico_AmiCoJavaPy_giveIntermediateReward(JNIEnv *, jobject, jfloat)
{
}

JNIEXPORT void JNICALL
Java_ch_idsia_tools_amico_AmiCoJavaPy_reset(JNIEnv *, jobject)
{
    
}