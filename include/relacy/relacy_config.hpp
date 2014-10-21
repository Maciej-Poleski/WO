#ifndef relacy_config_hpp
#define relacy_config_hpp

#ifdef _MSC_VER
#pragma once
#endif

#ifndef HAVE_STD_ATOMIC
/* #undef HAVE_STD_ATOMIC */
#endif
#ifndef HAVE__FUNC__
#define HAVE__FUNC__
#endif
#ifndef HAVE__FUNCTION__
#define HAVE__FUNCTION__
#endif
#ifndef RL_WIN
/* #undef RL_WIN */
#endif
#ifndef HAVE_INTRIN_H
/* #undef HAVE_INTRIN_H */
#endif
#ifndef HAVE_SYS_TIMES_H
#define HAVE_SYS_TIMES_H
#endif

#endif
